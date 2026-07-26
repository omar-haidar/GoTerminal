package dev.omar.goterminal.terminal;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.blankj.utilcode.util.ClipboardUtils;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalViewClient;

public class TerminalBackend implements TerminalViewClient, TerminalSessionClient {
    
    public interface SessionFinishedListener {
        void onSessionFinished(TerminalSession session);
    }
    
    private SessionFinishedListener sessionFinishedListener;
    
    public void setSessionFinishedListener(SessionFinishedListener listener) {
        this.sessionFinishedListener = listener;
    }

    @Override
    public void onTextChanged(TerminalSession changedSession) {

    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {

    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {
        if (sessionFinishedListener != null) {
            sessionFinishedListener.onSessionFinished(finishedSession);
        }
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
        return TerminalEmulator.TERMINAL_CURSOR_STYLE_BLOCK;
    }

    @Override
    public float onScale(float scale) {
        return scale;
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        // لا نحتاج لكتابة شيء هنا حالياً لأننا عالجنا النقر في الـ Adapter
        // ولكن يمكن استخدامه لاحقاً إذا لزم الأمر
    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return true; // قد يساعد في ظهور لوحة المفاتيح في بعض الأجهزة
    }

    @Override
    public boolean shouldUseCtrlSpaceWorkaround() {
        return false;
    }

    @Override
    public boolean isTerminalViewSelected() {
        return true;
    }

    @Override
    public void copyModeChanged(boolean copyMode) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
        if (session == null) return false;
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
