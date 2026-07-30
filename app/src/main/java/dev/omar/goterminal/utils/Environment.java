package dev.omar.goterminal.utils;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private static HashMap<String, String> env = new HashMap<>();

    @NonNull
    public static Map<String, String> getEnvironment() {
        if (env.isEmpty()) {
            env.put("PATH", createPath());
            env.put("TERM", "xterm-256color");
            env.put("COLORTERM", "truecolor");
            env.put("ROOTFS", TerminalInstaller.ROOTFS_PATH);
            env.put("HOME", TerminalInstaller.HOME_PATH);
            env.put("TMPDIR", TerminalInstaller.TMP_PATH);
            env.put("PROOT_TMP_DIR", TerminalInstaller.TMP_PATH);
            env.put("LD_LIBRARY_PATH", TerminalInstaller.LIB_PATH+":"+TerminalInstaller.PREFIX_PATH+"/lib");
        }
        return env;
    }

    private static String createPath() {
        String systemPath = System.getenv("PATH");
        String path =
                "/system/bin:"
                        + "/system/xbin:"
                        + TerminalInstaller.BIN_PATH
                        + ":"
                        + TerminalInstaller.PREFIX_PATH
                        + "/bin:"
                        + systemPath;
        return path;
    }

    public static String[] envToProps(Map<String, String> environment) {
        String[] env = new String[environment.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            env[index] = entry.getKey() + "=" + entry.getValue();
            index++;
        }
        return env;
    }

    public static void addToEnvIfPresent(Map<String, String> environment, String name) {
        String value = System.getenv(name);
        if (value != null) {
            environment.put(name, value);
        }
    }
}
