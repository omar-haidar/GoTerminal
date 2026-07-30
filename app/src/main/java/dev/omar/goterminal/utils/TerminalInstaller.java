package dev.omar.goterminal.utils;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class TerminalInstaller {

    public static final String DATA_PATH = "/data/data/dev.omar.goterminal/files";
    public static final String ROOTFS_PATH = DATA_PATH + "/rootfs/ubuntu-jammy-aarch64";
    public static final String HOME_PATH = DATA_PATH + "/home";
    public static final String BIN_PATH = DATA_PATH + "/bin";
    public static final String LIB_PATH = DATA_PATH + "/lib";
    public static final String TMP_PATH = DATA_PATH + "/tmp";

    public static final String PROOT_FILE_PATH = BIN_PATH + "/proot";
    public static final String BUSYBOX_FILE_PATH = BIN_PATH + "/busybox";

    private static final String INSTALLED_MARKER_FILE_PATH = ROOTFS_PATH + "/.installed";

    public static CompletableFuture<Result> installIfNeeded(Context context) {
        if (new File(INSTALLED_MARKER_FILE_PATH).exists()) {
            return CompletableFuture.completedFuture(new Result(true, "Already installed"));
        }
        return CompletableFuture.supplyAsync(() -> extractUbuntu(context));
    }

    @NonNull
    private static String getCompatAsset(String name) {
        return ArchUtils.getArch().concat("/").concat(name);
    }

    private static Result extractUbuntu(@NonNull Context context) {
        try {
            // Create directories
            FileUtils.createOrExistsDir(ROOTFS_PATH);
            FileUtils.createOrExistsDir(HOME_PATH);
            FileUtils.createOrExistsDir(BIN_PATH);
            FileUtils.createOrExistsDir(LIB_PATH);
            FileUtils.createOrExistsDir(TMP_PATH);

            // Copy Binaries
            ResourceUtils.copyFileFromAssets(getCompatAsset("proot"), PROOT_FILE_PATH);
            ResourceUtils.copyFileFromAssets("busybox", BUSYBOX_FILE_PATH);
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("libtalloc.so.2"), LIB_PATH + "/libtalloc.so.2");

            // Set Permissions for busybox before use
            Os.chmod(BUSYBOX_FILE_PATH, 0777);

            // Extract Ubuntu Rootfs
            File ubuntuTar = new File(context.getCacheDir(), "ubuntu.tar.xz");
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("ubuntu.tar.xz"), ubuntuTar.getAbsolutePath());
            
            Process process = new ProcessBuilder()
                    .command(BUSYBOX_FILE_PATH, "tar", "-xJf", ubuntuTar.getAbsolutePath(), "-C", new File(ROOTFS_PATH).getParent())
                    .redirectErrorStream(true)
                    .start();
            
            int exitCode = process.waitFor();
            ubuntuTar.delete();

            if (exitCode != 0) {
                return new Result(false, "Extraction failed with exit code: " + exitCode);
            }

            // Set Permissions for proot and libraries
            Os.chmod(PROOT_FILE_PATH, 0777);
            Os.chmod(LIB_PATH + "/libtalloc.so.2", 0755);

            // Fix APT Sandbox issues in Ubuntu
            File aptConfig = new File(ROOTFS_PATH, "etc/apt/apt.conf.d/999-no-sandbox");
            if (!aptConfig.getParentFile().exists()) aptConfig.getParentFile().mkdirs();
            com.blankj.utilcode.util.FileIOUtils.writeFileFromString(aptConfig, "APT::Sandbox::User \"root\";\n");

            // Create marker
            File marker = new File(INSTALLED_MARKER_FILE_PATH);
            if (!marker.getParentFile().exists()) marker.getParentFile().mkdirs();
            marker.createNewFile();

            return new Result(true, "Installation successful");
        } catch (IOException | ErrnoException | InterruptedException e) {
            return new Result(false, e.getMessage());
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
