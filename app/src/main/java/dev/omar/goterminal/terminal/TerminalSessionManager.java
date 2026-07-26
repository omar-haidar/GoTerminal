package dev.omar.goterminal.terminal;

import com.termux.terminal.TerminalSession;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import dev.omar.goterminal.terminal.factory.TerminalSessionFactory;
import dev.omar.goterminal.terminal.model.SessionConfig;

/**
 * SRP: Responsibility is managing the lifecycle and collection of TerminalSessions.
 * Repository Pattern: Acting as a data source for sessions.
 */
public class TerminalSessionManager {
    private final MutableLiveData<List<TerminalSession>> sessions = new MutableLiveData<>(new ArrayList<>());
    private final TerminalSessionFactory sessionFactory;
    private int sessionCounter = 0;

    public TerminalSessionManager(TerminalSessionFactory factory) {
        this.sessionFactory = factory;
    }

    public LiveData<List<TerminalSession>> getSessions() {
        return sessions;
    }

    public TerminalSession addSession(SessionConfig config) {
        TerminalSession session = sessionFactory.create(config);
        
        sessionCounter++;
        session.mSessionName = "Session " + sessionCounter;
        
        List<TerminalSession> currentSessions = new ArrayList<>(sessions.getValue());
        currentSessions.add(session);
        sessions.postValue(currentSessions);
        
        return session;
    }

    public void removeSession(TerminalSession session) {
        if (session == null) return;
        List<TerminalSession> currentSessions = new ArrayList<>(sessions.getValue());
        if (currentSessions.remove(session)) {
            session.finishIfRunning();
            sessions.postValue(currentSessions);
        }
    }
    
    public void removeSessionAt(int index) {
        List<TerminalSession> currentSessions = new ArrayList<>(sessions.getValue());
        if (index >= 0 && index < currentSessions.size()) {
            TerminalSession session = currentSessions.remove(index);
            session.finishIfRunning();
            sessions.postValue(currentSessions);
        }
    }
}
