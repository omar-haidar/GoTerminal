package dev.omar.goterminal.utils;

import java.util.ArrayList;
import java.util.List;

public class ProotLauncher {

    public static String[] buildProotCommand() {
        List<String> command = new ArrayList<>();

        // Proot Binary
        command.add(TerminalInstaller.PROOT_FILE_PATH);

        // Link with libtalloc (usually proot needs it in LD_LIBRARY_PATH, 
        // but some proot builds are static or have specific needs)

        // Root directory (The unzipped bootstrap)
        command.add("-r");
        command.add(TerminalInstaller.PREFIX_PATH);

        // Standard Bindings
        command.add("-b");
        command.add("/dev");
        command.add("-b");
        command.add("/proc");
        command.add("-b");
        command.add("/sys");
        command.add("-b");
        command.add("/data");

        // Bind SDCARD
        command.add("-b");
        command.add("/sdcard");

        // Working directory inside the rootfs
        command.add("-w");
        command.add("/home");

        // Command to execute (login shell)
        // Usually bootstrap has bash at /bin/bash or /usr/bin/bash
        command.add("/bin/bash");
        command.add("--login");

        return command.toArray(new String[0]);
    }

    public static String[] getProotEnv() {
        List<String> env = new ArrayList<>();
        env.add("PATH=/usr/bin:/bin:/usr/sbin:/sbin");
        env.add("HOME=/home");
        env.add("TERM=xterm-256color");
        env.add("LD_LIBRARY_PATH=/usr/lib:/lib");
        return env.toArray(new String[0]);
    }
}
