package dev.omar.goterminal.terminal.factory;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import dev.omar.goterminal.terminal.service.TerminalService;
import dev.omar.goterminal.terminal.service.TerminalServiceAction;

public class PendingIntentFactory {
    public static PendingIntent createExitPendingIntent(Context context) {
        Intent intent = new Intent(context, TerminalService.class);
        intent.setAction(TerminalServiceAction.ACTION_EXIT);
        return PendingIntent.getService(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static PendingIntent createWakeLockPendingIntent(Context context) {
        Intent intent = new Intent(context, TerminalService.class);
        intent.setAction(TerminalServiceAction.ACTION_WAKE_LOCK);
        return PendingIntent.getService(context, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
