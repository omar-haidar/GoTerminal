package dev.omar.goterminal.terminal;

import com.termux.view.TerminalView;

public interface TerminalFactory {

    public TerminalView createTerminal();

}
