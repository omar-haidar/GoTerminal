package dev.omar.goterminal.core.proot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.omar.goterminal.utils.TerminalInstaller;

public class TermuxPRootRunner implements EnvironmentRunner {

    private final String appFilesDir;
    private final String rootfsDir;
    private final String prootBinaryPath;

    public TermuxPRootRunner() {
        this.appFilesDir = TerminalInstaller.DATA_PATH;
        this.rootfsDir = TerminalInstaller.ROOTFS_PATH;
        this.prootBinaryPath = TerminalInstaller.PROOT_FILE_PATH; // مسار ثنائي proot الخاص بك
    }

    @Override
    public Process startEnvironment() throws IOException {
        List<String> command = new ArrayList<>();

        command.add(prootBinaryPath);
        command.add("-0");
        command.add("-r");
        command.add(rootfsDir);
        String termuxUsrPath = rootfsDir + "/data/data/com.termux/files/usr";
        command.add("-b");
        command.add(termuxUsrPath + ":/data/data/com.termux/files/usr");
        command.add("-b");
        command.add("/dev");
        command.add("-b");
        command.add("/proc");
        command.add("-b");
        command.add("/sys");
        command.add("-w");
        command.add("/data/data/com.termux/files/usr");
        command.add("/data/data/com.termux/files/usr/bin/bash");

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Map<String, String> env = processBuilder.environment();
        env.remove("LD_PRELOAD");
        env.put("HOME", "/data/data/com.termux/files/home");
        env.put("PREFIX", "/data/data/com.termux/files/usr");
        env.put("PATH", "/data/data/com.termux/files/usr/bin:/data/data/com.termux/files/usr/bin/applets");

        return processBuilder.start();
    }
}