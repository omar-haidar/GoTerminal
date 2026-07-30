package dev.omar.goterminal.utils;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TerminalInstaller {

    public static final String DATA_PATH = "/data/data/dev.omar.goterminal/files";
    public static final String ROOTFS_PATH = "/data/data/dev.omar.goterminal/files/rootfs";
    public static final String PREFIX_PATH = ROOTFS_PATH + "/data/data/com.termux/files/usr";
    public static final String HOME_PATH = DATA_PATH + "/home";
    public static final String BIN_PATH = DATA_PATH + "/bin";
    public static final String LIB_PATH = DATA_PATH + "/lib";
    public static final String TMP_PATH = DATA_PATH + "/tmp";

    public static final String PROOT_FILE_PATH = BIN_PATH + "/proot";
    public static final String BUSYBOX_FILE_PATH = BIN_PATH + "/busybox";

    private static final String INSTALLED_TERMINAL_MARKER_FILE_PATH =
            PREFIX_PATH + "/.terminal_installed";

    public static CompletableFuture<Result> installIfNeeded(Context context) {
        if (new File(INSTALLED_TERMINAL_MARKER_FILE_PATH).exists()) {
            return CompletableFuture.completedFuture(new Result(true, "Already installed"));
        }
        return CompletableFuture.supplyAsync(() -> extractBootstrap(context));
    }

    @NonNull
    private static String getCompatAsset(String name) {
        return ArchUtils.getArch().concat("/").concat(name);
    }

    private static Result extractBootstrap(@NonNull Context context) {
        try {
            // Create directories
            FileUtils.createOrExistsDir(ROOTFS_PATH);
            FileUtils.createOrExistsDir(PREFIX_PATH);
            FileUtils.createOrExistsDir(HOME_PATH);
            FileUtils.createOrExistsDir(BIN_PATH);
            FileUtils.createOrExistsDir(LIB_PATH);
            FileUtils.createOrExistsDir(TMP_PATH);

            // Ensure directory for login/bash exists in guest path
            FileUtils.createOrExistsDir(PREFIX_PATH + "/bin");
            FileUtils.createOrExistsDir(PREFIX_PATH + "/tmp");

            // Copy Binaries
            ResourceUtils.copyFileFromAssets(getCompatAsset("proot"), PROOT_FILE_PATH);
            ResourceUtils.copyFileFromAssets("busybox", BUSYBOX_FILE_PATH);
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("libtalloc.so.2"), LIB_PATH + "/libtalloc.so.2");

            // Extract Bootstrap to usr
            File bootstrapZip = new File(context.getCacheDir(), "bootstrap.zip");
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("bootstrap.zip"), bootstrapZip.getAbsolutePath());
            var result = extractBootstrapArchive(bootstrapZip);
            if (!result.success) return result;
            bootstrapZip.delete();

            // Set Permissions
            Os.chmod(PROOT_FILE_PATH, 0777);
            Os.chmod(BUSYBOX_FILE_PATH, 0777);
            Os.chmod(LIB_PATH + "/libtalloc.so.2", 0755);

            // Create marker
            File marker = new File(INSTALLED_TERMINAL_MARKER_FILE_PATH);
            if (marker.exists()) marker.delete();
            marker.createNewFile();

            return new Result(true, "Installation successful");
        } catch (IOException | ErrnoException e) {
            return new Result(false, e.getMessage());
        }
    }

    private static Result extractBootstrapArchive(File bootstrapFile) {
        final List<LinkedPath> symlinks = new ArrayList<>(128);
        final byte[] buffer = new byte[16384];

        try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(bootstrapFile))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();

                if (entryName.equals("SYMLINKS.txt")) {
                    StringBuilder sb = new StringBuilder();
                    int read;
                    byte[] symBuffer = new byte[4096];
                    while ((read = zipStream.read(symBuffer)) != -1) {
                        sb.append(new String(symBuffer, 0, read));
                    }

                    for (String line : sb.toString().split("\n")) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("←");
                        if (parts.length == 2) {
                            symlinks.add(new LinkedPath(parts[0], PREFIX_PATH + "/" + parts[1]));
                        }
                    }
                } else {
                    File targetFile = new File(PREFIX_PATH, entryName);

                    if (entry.isDirectory()) {
                        if (!targetFile.exists() && !targetFile.mkdirs()) {
                            throw new IOException("Failed to create directory: " + targetFile);
                        }
                    } else {
                        File parent = targetFile.getParentFile();
                        if (parent != null && !parent.exists() && !parent.mkdirs()) {
                            throw new IOException("Failed to create parent: " + parent);
                        }

                        try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                            int len;
                            while ((len = zipStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, len);
                            }
                        }

                        if (shouldSetExecutable(entryName)) {
                            Os.chmod(targetFile.getAbsolutePath(), 0700);
                        }
                    }
                }
                zipStream.closeEntry();
            }

            // Create Symlinks after all files are extracted
            for (LinkedPath symlink : symlinks) {
                File linkFile = new File(symlink.newPath);
                if (linkFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    linkFile.delete();
                }
                try {
                    Os.symlink(symlink.oldPath, symlink.newPath);
                } catch (ErrnoException ignored) {
                }
            }

            return new Result(true, "Bootstrap extracted successfully");
        } catch (Exception e) {
            return new Result(false, "Extraction error: " + e.getMessage());
        }
    }

    private static boolean shouldSetExecutable(@NonNull String path) {
        return path.contains("/bin/") ||
                path.contains("/libexec/") ||
                path.contains("/lib/apt/methods/") ||
                path.endsWith("/login") ||
                path.endsWith("/bash") ||
                path.endsWith("/sh");
    }

    public static class LinkedPath {
        final String oldPath;
        final String newPath;

        public LinkedPath(String oldPath, String newPath) {
            this.oldPath = oldPath;
            this.newPath = newPath;
        }
    }

    public static class Result {
        private final boolean success;
        private final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
