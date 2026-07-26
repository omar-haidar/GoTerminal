package dev.omar.goterminal;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.termux.terminal.TerminalSession;

import java.util.ArrayList;
import java.util.List;

import dev.omar.goterminal.terminal.TerminalBackend;
import dev.omar.goterminal.terminal.TerminalService;

public class MainViewModel extends AndroidViewModel {
    
    private final MutableLiveData<TerminalService> terminalService = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isBound = new MutableLiveData<>(false);
    private final MediatorLiveData<List<TerminalSession>> sessions = new MediatorLiveData<>();
    private LiveData<List<TerminalSession>> currentServiceSessions = null;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TerminalService.TerminalServiceBinder binder = (TerminalService.TerminalServiceBinder) service;
            TerminalService s = binder.getService();
            terminalService.setValue(s);
            isBound.setValue(true);
            
            if (currentServiceSessions != null) {
                sessions.removeSource(currentServiceSessions);
            }
            currentServiceSessions = s.getSessions();
            sessions.addSource(currentServiceSessions, value -> sessions.setValue(value));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            terminalService.setValue(null);
            isBound.setValue(false);
            
            if (currentServiceSessions != null) {
                sessions.removeSource(currentServiceSessions);
                currentServiceSessions = null;
            }
            sessions.setValue(new ArrayList<>());
        }
    };

    public MainViewModel(Application app) {
        super(app);
        sessions.setValue(new ArrayList<>());
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(getApplication(), TerminalService.class);
        getApplication().startService(intent); // Ensure service stays alive
        getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public LiveData<TerminalService> getTerminalService() {
        return terminalService;
    }

    public LiveData<Boolean> getIsBound() {
        return isBound;
    }
    
    public LiveData<List<TerminalSession>> getSessions() {
        return sessions;
    }

    public void addNewSession() {
        if (Boolean.TRUE.equals(isBound.getValue()) && terminalService.getValue() != null) {
            terminalService.getValue().createNewSession();
        }
    }
    
    public void removeSession(TerminalSession session) {
        if (Boolean.TRUE.equals(isBound.getValue()) && terminalService.getValue() != null) {
            terminalService.getValue().removeSession(session);
        }
    }
    
    public TerminalBackend getTerminalBackend() {
        return terminalService.getValue() != null ? terminalService.getValue().getTerminalBackend() : null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (Boolean.TRUE.equals(isBound.getValue())) {
            getApplication().unbindService(serviceConnection);
            isBound.setValue(false);
        }
        
        if (currentServiceSessions != null) {
            sessions.removeSource(currentServiceSessions);
        }
    }
}
