package dev.omar.goterminal;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.termux.terminal.TerminalSession;
import java.util.List;
import dev.omar.goterminal.terminal.TerminalBackend;
import dev.omar.goterminal.terminal.TerminalService;

public class MainViewModel extends AndroidViewModel {
    
    private final TerminalService terminalService;

    public MainViewModel(Application app) {
        super(app);
        terminalService = new TerminalService();
    }

    public LiveData<List<TerminalSession>> getSessions() {
        return terminalService.getSessions();
    }

    public void addNewSession() {
        terminalService.createNewSession(getApplication());
    }
    
    public void removeSession(TerminalSession session) {
        terminalService.removeSession(session);
    }
    
    public TerminalBackend getTerminalBackend() {
        return terminalService.getTerminalBackend();
    }
}
