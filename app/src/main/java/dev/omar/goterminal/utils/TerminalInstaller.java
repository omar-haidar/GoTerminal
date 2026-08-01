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
    public static final String BASH_FILE_PATH = DATA_PATH + "/rootfs/ubuntu-jammy-aarch64/bin/bash";
    public static final String HOME_PATH = DATA_PATH + "/home";
    public static final String LOCAL_PATH = DATA_PATH + "/local";
    public static final String BIN_PATH = DATA_PATH + "/bin";
    public static final String LIB_PATH = DATA_PATH + "/lib";
    public static final String TMP_PATH = ROOTFS_PATH + "/tmp";
    public static final String PROOT_FILE_PATH = BIN_PATH + "/proot";
    public static final String BUSYBOX_FILE_PATH = BIN_PATH + "/busybox";
    public static final String INIT_SCRIPT_FILE_PATH = BIN_PATH + "/init";
    public static final String INIT_HOST_FILE_PATH = BIN_PATH + "/init-host";
    public static final String UTILS_SCRIPT_FILE_PATH = BIN_PATH + "/utils";

    private static final String INSTALLED_MARKER_FILE_PATH = ROOTFS_PATH + "/.installed";

    public static CompletableFuture<Result> installIfNeeded(Context context) {
        return extractInitScript()
                .thenApply(
                        t -> {
                            if (new File(INSTALLED_MARKER_FILE_PATH).exists()) {
                                return (new Result(true, "Already installed"));
                            }
                            return extractUbuntu(context);
                        });

    }

    private static CompletableFuture<Result> extractInitScript() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        ResourceUtils.copyFileFromAssets("scripts/init.sh", INIT_SCRIPT_FILE_PATH);
                        ResourceUtils.copyFileFromAssets("scripts/init-host.sh", INIT_HOST_FILE_PATH);
                        ResourceUtils.copyFileFromAssets("scripts/utils.sh", UTILS_SCRIPT_FILE_PATH);
                        Os.chmod(INIT_SCRIPT_FILE_PATH, 0777);
                        Os.chmod(INIT_HOST_FILE_PATH, 0777);
                        Os.chmod(UTILS_SCRIPT_FILE_PATH, 0777);
                        return new Result(true, "Scripts-installed!");
                    } catch (Exception err) {
                        return new Result(false, err.getMessage());
                    }
                });
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
            FileUtils.createOrExistsDir(LOCAL_PATH);
            FileUtils.createOrExistsDir(BIN_PATH);
            FileUtils.createOrExistsDir(LIB_PATH);
            FileUtils.createOrExistsDir(TMP_PATH);

            ResourceUtils.copyFileFromAssets("stat", LOCAL_PATH + "/stat");
            ResourceUtils.copyFileFromAssets("vmstat", LOCAL_PATH + "/vmstat");

            ResourceUtils.copyFileFromAssets("init.sh", INIT_SCRIPT_FILE_PATH);
            ResourceUtils.copyFileFromAssets("init-host.sh", INIT_HOST_FILE_PATH);
            Os.chmod(INIT_SCRIPT_FILE_PATH, 0777);
            Os.chmod(INIT_HOST_FILE_PATH, 0777);

            ResourceUtils.copyFileFromAssets(getCompatAsset("proot"), PROOT_FILE_PATH);
            ResourceUtils.copyFileFromAssets("busybox", BUSYBOX_FILE_PATH);
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("libtalloc.so.2"), LIB_PATH + "/libtalloc.so.2");

            // Set Permissions for proot and libraries
            Os.chmod(PROOT_FILE_PATH, 0777);
            Os.chmod(LIB_PATH + "/libtalloc.so.2", 0755);
            Os.chmod(BUSYBOX_FILE_PATH, 0777);

            // Extract Ubuntu Rootfs
            File ubuntuTar = new File(context.getCacheDir(), "ubuntu.tar.xz");
            ResourceUtils.copyFileFromAssets(
                    getCompatAsset("ubuntu.tar.xz"), ubuntuTar.getAbsolutePath());

            Process process =
                    new ProcessBuilder()
                            .command(
                                    BUSYBOX_FILE_PATH,
                                    "tar",
                                    "-xJf",
                                    ubuntuTar.getAbsolutePath(),
                                    "-C",
                                    new File(ROOTFS_PATH).getParent())
                            .redirectErrorStream(true)
                            .start();

            int exitCode = process.waitFor();
            ResourceUtils.copyFileFromAssets("resolv.conf", ROOTFS_PATH + "/etc/resolv.conf");
            ubuntuTar.delete();

            if (exitCode != 0) {
                return new Result(false, "Extraction failed with exit code: " + exitCode);
            }

            
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
