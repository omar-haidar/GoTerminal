package dev.omar.goterminal.utils;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TerminalInstaller {

    public static final String DATA_PATH = "/data/data/dev.omar.goterminal/files";
    public static final String ROOTFS_PATH = "/data/data/dev.omar.goterminal/files/rootfs";
    public static final String PREFIX_PATH = ROOTFS_PATH + "/data/data/com.termux/files/usr";
    public static final String HOME_PATH = DATA_PATH + "/home";
    public static final String BIN_PATH = DATA_PATH + "/bin";
    public static final String LIB_PATH = DATA_PATH + "/lib";
    public static final String TMP_PATH = PREFIX_PATH + "/tmp";

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

    private static Result extractBootstrap(Context context) {
        try {
            // Create directories
            FileUtils.createOrExistsDir(ROOTFS_PATH);
            FileUtils.createOrExistsDir(PREFIX_PATH);
            FileUtils.createOrExistsDir(HOME_PATH);
            FileUtils.createOrExistsDir(BIN_PATH);
            FileUtils.createOrExistsDir(LIB_PATH);

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
        try {
            final ZipInputStream zip = new ZipInputStream(new FileInputStream(bootstrapFile));
            final byte[] buffer = new byte[8096];
            final List<LinkedPath> symlinks = new ArrayList<LinkedPath>(50);
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("SYMLINKS.txt")) {
                    final BufferedReader symlinksReader =
                            new BufferedReader(new InputStreamReader(zip));
                    String line;
                    while ((line = symlinksReader.readLine()) != null) {
                        String[] parts = line.split("←");
                        if (parts.length != 2) {
                            final var err = "Malformed symlink line: " + line;

                            return new Result(false, err);
                        }
                        String oldPath = parts[0];
                        String newPath = PREFIX_PATH + "/" + parts[1];
                        symlinks.add(new LinkedPath(oldPath, newPath));

                        final File parentFile = new File(newPath).getParentFile();
                        if (!FileUtils.createOrExistsDir(parentFile)) {

                            throw new IOException("Unable to create directory: " + parentFile);
                        }
                    }
                } else {
                    String zipEntryName = entry.getName();
                    File targetFile = new File(PREFIX_PATH, zipEntryName);
                    boolean isDirectory = entry.isDirectory();

                    final var dir = isDirectory ? targetFile : targetFile.getParentFile();
                    if (dir != null && !dir.exists() && !dir.mkdirs()) {

                        throw new IOException("Unable to create directory: " + dir);
                    }

                    // If the file exists and it is not a directory
                    // Delete that file
                    final var targetFilePath = targetFile.toPath();
                    if (Files.exists(targetFilePath) && !Files.isDirectory(targetFilePath)) {
                        try {
                            Files.delete(targetFilePath);
                        } catch (Throwable th) {
                            throw new CompletionException(th);
                        }
                    }

                    if (!isDirectory) {
                        try (final var outStream = new FileOutputStream(targetFile)) {
                            int readBytes;
                            while ((readBytes = zip.read(buffer)) != -1)
                                outStream.write(buffer, 0, readBytes);
                        }

                        if (zipEntryName.startsWith("bin/")
                                || zipEntryName.startsWith("libexec")
                                || zipEntryName.startsWith("lib/apt/apt-helper")
                                || zipEntryName.startsWith("lib/apt/methods")) {

                            //noinspection OctalInteger
                            Os.chmod(targetFile.getAbsolutePath(), 0700);
                        }
                    }
                }
                if (symlinks.isEmpty()) {

                    throw new IOException(("No SYMLINKS.txt encountered"));
                }

                for (LinkedPath symlink : symlinks) {

                    final var target = Paths.get(symlink.newPath);
                    if (Files.exists(target) && !Files.isDirectory(target)) {
                        try {
                            Files.delete(target);
                        } catch (Throwable throwable) {
                            throw new CompletionException(throwable);
                        }
                    }
                    Os.symlink(symlink.oldPath, symlink.newPath);
                }
            }
            return new Result(true, "");
        } catch (Exception err) {
            return new Result(false, err.getMessage());
        }
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
