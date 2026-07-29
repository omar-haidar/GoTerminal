package dev.omar.goterminal.terminal.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import dev.omar.goterminal.MainActivity;
import dev.omar.goterminal.R;
import dev.omar.goterminal.terminal.factory.PendingIntentFactory;

public class TerminalService extends Service {

    private static final String CHANNEL_ID = "terminal_service_channel";
    private static final int NOTIFICATION_ID = 2002;
    private final IBinder binder = new TerminalServiceBinder(this);

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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

    @NonNull
    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        int sessionCount = 0;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GoTerminal")
                .setContentText(sessionCount + " active sessions")
                .setSmallIcon(dev.omar.goterminal.R.drawable.ic_comedy_mask)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_close, "Exit", PendingIntentFactory.createExitPendingIntent(this))
                .addAction(R.drawable.ic_layers_plus, "WAke Lock", PendingIntentFactory.createWakeLockPendingIntent(this))
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
        return START_NOT_STICKY;
    }
}
