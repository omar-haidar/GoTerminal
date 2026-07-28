package dev.omar.goterminal.terminal.model;

public class SessionConfig {
    private final String shellPath;
    private final String workingDirectory;
    private final String[] environmentVariables;
    private final String[] arguments;

    public SessionConfig(String shellPath, String workingDirectory, String[] env, String[] args) {
        this.shellPath = shellPath;
        this.workingDirectory = workingDirectory;
        this.environmentVariables = env;
        this.arguments = args;
    }

    public String getShellPath() {
        return shellPath;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String[] getEnvironmentVariables() {
        return environmentVariables;
    }

    public String[] getArguments() {
        return arguments;
    }
}
