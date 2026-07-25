package dev.omar.goterminal.utils;

import android.system.ErrnoException;
import android.system.Os;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ResourceUtils;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TerminalInstaller {

    private static final String DATA_PATH = "/data/data/dev.omar.goterminal";
    private static final String PREFIX_PATH = DATA_PATH + "/files/data/data/com.termux/files/usr";
    private static final String CACHE_PATH = DATA_PATH + "/cache";
    private static final String HOME_PATH = DATA_PATH + "/files/home";
    private static final String PROOT_FILE_PATH = HOME_PATH + "/proot";
    private static final String LIBTALLOC_FILE_PATH = HOME_PATH + "/libtalloc.so.2";
    private static final String BUSYBOX_FILE_PATH = HOME_PATH + "/proot";
    private static final String INSTALLED_TERMINAL_MARKER_FILE_PATH = PREFIX_PATH + "/.terminal_installed";

    @Nullable
    @Contract(pure = true)
    public static CompletableFuture<Result> installIfNeeded() {
        if (new File(INSTALLED_TERMINAL_MARKER_FILE_PATH).exists()) {
            return CompletableFuture.completedFuture(new Result(true));
        }
        return extractBootstrap();
    }

    private static CompletableFuture<Result> extractBootstrap() {
        final String bootstrapFilePath = CACHE_PATH + "/bootstrap.zip";
        if (!new File(bootstrapFilePath).exists()) {
            ResourceUtils.copyFileFromAssets("arm64-v8a/bootstrap.zip", bootstrapFilePath);
        }
        if (!new File(BUSYBOX_FILE_PATH).exists()) {
            ResourceUtils.copyFileFromAssets("arm64-v8a/busybox", BUSYBOX_FILE_PATH);
        }
        if (!new File(PROOT_FILE_PATH).exists() || !new File(LIBTALLOC_FILE_PATH).exists()) {
            ResourceUtils.copyFileFromAssets("arm64-v8a/proot", PROOT_FILE_PATH);
            ResourceUtils.copyFileFromAssets("arm64-v8a/libtalloc.so.2", LIBTALLOC_FILE_PATH);
        }
        try {
            Os.chmod(BUSYBOX_FILE_PATH,777);
            Os.chmod(PROOT_FILE_PATH,777);
        } catch (ErrnoException e) {
            return CompletableFuture.completedFuture(new Result(e));
        }

        return CompletableFuture.completedFuture(new Result(false,"Null installing error!"));
    }

    public static class Result {
        private final boolean success;
        private final String message;
        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public Result(boolean success) {
            this(success,"");
        }

        public Result(Throwable th) {
           this(false,th.getMessage());
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

}
