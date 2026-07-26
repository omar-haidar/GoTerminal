package dev.omar.goterminal.terminal;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.blankj.utilcode.util.ClipboardUtils;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalViewClient;

public class TerminalBackend implements TerminalViewClient, TerminalSessionClient {
    @Override
    public void onTextChanged(TerminalSession changedSession) {

    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {

    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {

    }

    @Override
    public void onCopyTextToClipboard(TerminalSession session, String text) {
        ClipboardUtils.copyText("GoTerminal",text);
    }

    @Override
    public void onPasteTextFromClipboard(TerminalSession session) {

    }

    @Override
    public void onBell(TerminalSession session) {

    }

    @Override
    public void onColorsChanged(TerminalSession session) {

    }

    @Override
    public void onTerminalCursorStateChange(boolean state) {

    }

    @Override
    public Integer getTerminalCursorStyle() {
        return 0;
    }

    @Override
    public float onScale(float scale) {
        return TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE;
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {

    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return false;
    }

    @Override
    public boolean shouldUseCtrlSpaceWorkaround() {
        return false;
    }

    @Override
    public boolean isTerminalViewSelected() {
        return false;
    }

    @Override
    public void copyModeChanged(boolean copyMode) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        return false;
    }

    @Override
    public boolean onLongPress(MotionEvent event) {
        return false;
    }

    @Override
    public boolean readControlKey() {
        return false;
    }

    @Override
    public boolean readAltKey() {
        return false;
    }

    @Override
    public boolean readShiftKey() {
        return false;
    }

    @Override
    public boolean readFnKey() {
        return false;
    }

    @Override
    public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
        return false;
    }

    @Override
    public void onEmulatorSet() {

    }

    @Override
    public void logError(String tag, String message) {

    }

    @Override
    public void logWarn(String tag, String message) {

    }

    @Override
    public void logInfo(String tag, String message) {

    }

    @Override
    public void logDebug(String tag, String message) {

    }

    @Override
    public void logVerbose(String tag, String message) {

    }

    @Override
    public void logStackTraceWithMessage(String tag, String message, Exception e) {

    }

    @Override
    public void logStackTrace(String tag, Exception e) {

    }
}
