package dev.omar.goterminal.terminal.service;

import android.os.Binder;

public class TerminalServiceBinder extends Binder {
    private TerminalService service;
    public TerminalServiceBinder(TerminalService service) {
        this.service = service;
    }

    public TerminalService getService() {
        return service;
    }
}
