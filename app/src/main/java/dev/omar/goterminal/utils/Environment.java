package dev.omar.goterminal.utils;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    @NonNull
    public static Map<String, String> getEnvironment() {
        HashMap<String, String> env = new HashMap<>();

        String prefix = "/data/data/com.termux/files/usr";
        String home = "/data/data/com.termux/files/home";

        env.put("PATH", prefix + "/bin:" + prefix + "/bin/applets:/system/bin:/system/xbin");
        env.put("PREFIX", prefix);
        env.put("HOME", home);
        env.put("TMPDIR", prefix + "/tmp");
        env.put("TERM", "xterm-256color");
        env.put("COLORTERM", "truecolor");

        // إعدادات proot للتعامل مع قيود أندرويد الحديثة
        env.put("PROOT_NO_SECCOMP", "1");
        env.put("PROOT_FORCE_PTRACE_ONLY", "1");
        env.put("PROOT_TMP_DIR", TerminalInstaller.TMP_PATH);

        // مسار المكتبات الشامل (المحلية ونظام أندرويد)
        env.put("LD_LIBRARY_PATH", TerminalInstaller.LIB_PATH + ":/system/lib64:/system/lib");

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
