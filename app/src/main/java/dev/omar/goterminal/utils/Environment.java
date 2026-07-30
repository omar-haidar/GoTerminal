package dev.omar.goterminal.utils;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

public final class Environment {

    @NonNull
    public static Map<String, String> getEnvironment() {
        HashMap<String, String> env = new HashMap<>();
        
        // Standard Linux environment variables for Ubuntu
        env.put("PATH", "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
        env.put("HOME", "/root");
        env.put("TERM", "xterm-256color");
        env.put("LANG", "en_US.UTF-8");
        env.put("SHELL", "/bin/bash");
        
        // Proot specific variables
        env.put("PROOT_NO_SECCOMP", "1");
        env.put("PROOT_FORCE_PTRACE_ONLY", "1");
        env.put("PROOT_TMP_DIR", TerminalInstaller.TMP_PATH);
        
        // Disable APT sandboxing via environment as well
        env.put("APT_CONFIG", "/etc/apt/apt.conf.d/999-no-sandbox");
        
        // Library path for proot dependencies
        env.put("LD_LIBRARY_PATH", TerminalInstaller.LIB_PATH + ":/lib:/usr/lib");

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
