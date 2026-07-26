package dev.omar.goterminal.terminal;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.termux.terminal.TerminalSession;
import java.util.List;
import dev.omar.goterminal.terminal.factory.TerminalSessionFactory;
import dev.omar.goterminal.terminal.model.SessionConfig;
import dev.omar.goterminal.utils.ProotLauncher;
import dev.omar.goterminal.utils.TerminalInstaller;

/**
 * Facade Pattern: Unified interface to a set of interfaces in a subsystem.
 */
public class TerminalService {
    
    private final TerminalSessionManager sessionManager;
    private final TerminalBackend terminalBackend;

    public TerminalService() {
        this.terminalBackend = new TerminalBackend();
        TerminalSessionFactory factory = new TerminalSessionFactory(terminalBackend);
        this.sessionManager = new TerminalSessionManager(factory);
        
        // Connect backend to manager for automatic cleanup
        this.terminalBackend.setSessionFinishedListener(sessionManager::removeSession);
    }

    public LiveData<List<TerminalSession>> getSessions() {
        return sessionManager.getSessions();
    }

    public void createNewSession(Context context) {
        SessionConfig config;
        
        // Check if proot is installed to decide which session to create
        if (new java.io.File(TerminalInstaller.PROOT_FILE_PATH).exists()) {
            String[] prootCmd = ProotLauncher.buildProotCommand();
            String[] prootEnv = ProotLauncher.getProotEnv();
            
            // The first element is the binary, rest are args
            String shell = prootCmd[0];
            String[] args = new String[prootCmd.length - 1];
            System.arraycopy(prootCmd, 1, args, 0, args.length);
            
            config = new SessionConfig(
                    shell,
                    "/home",
                    prootEnv,
                    args
            );
        } else {
            // Fallback to Android Shell
            String homeDir = context.getFilesDir().getPath();
            config = new SessionConfig(
                    "/system/bin/sh",
                    homeDir,
                    new String[]{"PATH=/system/bin:/system/xbin"},
                    new String[]{}
            );
        }

        sessionManager.addSession(config);
    }

    public void removeSession(TerminalSession session) {
        sessionManager.removeSession(session);
    }

    public TerminalBackend getTerminalBackend() {
        return terminalBackend;
    }
}
