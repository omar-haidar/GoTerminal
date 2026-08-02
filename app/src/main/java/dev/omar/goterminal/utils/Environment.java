package dev.omar.goterminal.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Environment {

    public static final String DATA_PATH = "/data/data/dev.omar.goterminal/files";
    public static final String CACHE_PATH = "/data/data/dev.omar.goterminal/cache";
    public static final String ROOTFS_PATH = DATA_PATH + "/rootfs";
    public static final String BASH_FILE_PATH = ROOTFS_PATH + "/bin/sh";
    public static final String HOME_PATH = DATA_PATH + "/home";
    public static final String LOCAL_PATH = DATA_PATH + "/local";
    public static final String BIN_PATH = DATA_PATH + "/bin";
    public static final String LIB_PATH = DATA_PATH + "/lib";
    public static final String LIBTALLOC_FILE_PATH = LIB_PATH + "/libtalloc.so.2";
    public static final String TMP_PATH = ROOTFS_PATH + "/tmp";
    public static final String PROOT_FILE_PATH = BIN_PATH + "/proot";
    public static final String BUSYBOX_FILE_PATH = BIN_PATH + "/busybox";
    public static final String INIT_SCRIPT_FILE_PATH = BIN_PATH + "/init";
    public static final String INIT_HOST_FILE_PATH = BIN_PATH + "/init-host";
    public static final String UTILS_SCRIPT_FILE_PATH = BIN_PATH + "/utils";

    private static final String INSTALLED_MARKER_FILE_PATH = ROOTFS_PATH + "/.installed";

    @NonNull
    public static Map<String, String> getEnvironment() {
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("ANDROID_ART_ROOT", System.getenv("ANDROID_ART_ROOT"));
        envVariables.put("ANDROID_DATA", System.getenv("ANDROID_DATA"));
        envVariables.put("ANDROID_I18N_ROOT", System.getenv("ANDROID_I18N_ROOT"));
        envVariables.put("ANDROID_ROOT", System.getenv("ANDROID_ROOT"));
        envVariables.put("ANDROID_RUNTIME_ROOT", System.getenv("ANDROID_RUNTIME_ROOT"));
        envVariables.put("ANDROID_TZDATA_ROOT", System.getenv("ANDROID_TZDATA_ROOT"));
        envVariables.put("BOOTCLASSPATH", System.getenv("BOOTCLASSPATH"));
        envVariables.put("DEX2OATBOOTCLASSPATH", System.getenv("DEX2OATBOOTCLASSPATH"));
        envVariables.put("EXTERNAL_STORAGE", System.getenv("EXTERNAL_STORAGE"));

        HashMap<String, String> env = new HashMap<>();
        env.putAll(envVariables);

        env.put("PATH", System.getenv("PATH") + ":/bin:/sbin:/usr/bin:/usr/sbin:/usr/share/bin:/usr/share/sbin:/usr/local/bin:/usr/local/sbin:/system/bin:/system/xbin:" + TerminalInstaller.BIN_PATH);
        env.put("HOME", "/sdcard");
        env.put("COLORTERM", "truecolor");
        env.put("TERM", "xterm-256color");
        env.put("PS1", "\\[\\033[01;32m\\]\\u@reterm\\[\\033[00m\\]:\\[\\033[01;34m\\]\\w\\[\\033[00m\\]\\");
        env.put("LANG", "C.UTF-8");
        env.put("BIN", TerminalInstaller.BIN_PATH);
        env.put("PREFIX", TerminalInstaller.DATA_PATH);
        env.put("ROOTFS", TerminalInstaller.ROOTFS_PATH);
        env.put("LD_LIBRARY_PATH", TerminalInstaller.LIB_PATH);
        env.put("LINKER", new File("/system/bin/linker64").exists() ? "/system/bin/linker64" : "/system/bin/linker");
        env.put("PROOT_TMP_DIR", TerminalInstaller.TMP_PATH);
        env.put("TMPDIR",TerminalInstaller.TMP_PATH);
        env.put("PROOT", TerminalInstaller.PROOT_FILE_PATH);
        env.put("INIT_SCRIPT", TerminalInstaller.INIT_SCRIPT_FILE_PATH);
        env.put("INIT_HOST_SCRIPT", TerminalInstaller.INIT_HOST_FILE_PATH);

        return env;
    }

    @NonNull
    public static String[] envToProps(@NonNull Map<String, String> environment) {
        String[] env = new String[environment.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            env[index] = entry.getKey() + "=" + entry.getValue();
            index++;
        }
        return env;
    }
}
