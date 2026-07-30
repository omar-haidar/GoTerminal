package dev.omar.goterminal.terminal.factory;

import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;

import dev.omar.goterminal.terminal.model.SessionConfig;

public class TerminalSessionFactory {

    private final TerminalSessionClient sessionClient;
    private static final int TRANSCRIPT_ROWS = 2000;

    public TerminalSessionFactory(TerminalSessionClient sessionClient) {
        this.sessionClient = sessionClient;
    }

    public TerminalSession create(SessionConfig config) {
        return new TerminalSession(
                config.getShellPath(),
                config.getWorkingDirectory(),
                config.getArguments(),
                config.getEnvironmentVariables(),
                TRANSCRIPT_ROWS,
                sessionClient
        );
    }
}
