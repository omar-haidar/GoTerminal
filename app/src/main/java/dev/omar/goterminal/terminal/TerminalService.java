package dev.omar.goterminal.terminal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.termux.terminal.TerminalSession;

import java.util.ArrayList;
import java.util.List;

import dev.omar.goterminal.MainActivity;
import dev.omar.goterminal.R;
import dev.omar.goterminal.terminal.factory.TerminalSessionFactory;
import dev.omar.goterminal.terminal.model.SessionConfig;
import dev.omar.goterminal.utils.ProotLauncher;
import dev.omar.goterminal.utils.TerminalInstaller;

public class TerminalService extends Service {

    private static final String CHANNEL_ID = "terminal_service_channel";
    private static final int NOTIFICATION_ID = 1337;

    private final IBinder binder = new TerminalServiceBinder();
    private final MutableLiveData<List<TerminalSession>> sessions = new MutableLiveData<>(new ArrayList<>());
    private TerminalBackend terminalBackend;
    private TerminalSessionFactory sessionFactory;
    private int sessionCounter = 0;

    public class TerminalServiceBinder extends Binder {
        public TerminalService getService() {
            return TerminalService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        terminalBackend = new TerminalBackend();
        sessionFactory = new TerminalSessionFactory(terminalBackend);
        
        terminalBackend.setSessionFinishedListener(this::removeSession);
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public LiveData<List<TerminalSession>> getSessions() {
        return sessions;
    }

    public void createNewSession() {
        SessionConfig config;
        
        if (new java.io.File(TerminalInstaller.PROOT_FILE_PATH).exists()) {
            String[] prootCmd = ProotLauncher.buildProotCommand();
            String[] prootEnv = ProotLauncher.getProotEnv();
            
            String shell = prootCmd[0];
            String[] args = new String[prootCmd.length - 1];
            System.arraycopy(prootCmd, 1, args, 0, args.length);
            
            config = new SessionConfig(shell, "/home", prootEnv, args);
        } else {
            String homeDir = getFilesDir().getPath();
            config = new SessionConfig("/system/bin/sh", homeDir, new String[]{"PATH=/system/bin:/system/xbin"}, new String[]{});
        }
        
        TerminalSession session = sessionFactory.create(config);
        sessionCounter++;
        session.mSessionName = "Session " + sessionCounter;
        
        List<TerminalSession> currentSessions = new ArrayList<>(sessions.getValue());
        currentSessions.add(session);
        sessions.postValue(currentSessions);
        
        updateNotification();
    }

    public void removeSession(TerminalSession session) {
        List<TerminalSession> currentSessions = new ArrayList<>(sessions.getValue());
        if (currentSessions.remove(session)) {
            session.finishIfRunning();
            sessions.postValue(currentSessions);
            updateNotification();
            
            if (currentSessions.isEmpty()) {
                stopForeground(true);
                stopSelf();
            }
        }
    }

    public TerminalBackend getTerminalBackend() {
        return terminalBackend;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Terminal Sessions",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        int sessionCount = sessions.getValue() != null ? sessions.getValue().size() : 0;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GoTerminal")
                .setContentText(sessionCount + " active sessions")
                .setSmallIcon(dev.omar.goterminal.R.drawable.ic_comedy_mask)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification());
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
