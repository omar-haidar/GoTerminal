package dev.omar.goterminal.terminal;

import android.graphics.Color;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.color.MaterialColors;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

public class TerminalBackend implements TerminalViewClient, TerminalSessionClient {

    private TerminalView terminalView;
    private int terminalTextSize = 18;

    public int getTerminalTextSize() {
        return terminalTextSize;
    }

    public void setTerminalTextSize(int terminalTextSize) {
        this.terminalTextSize = terminalTextSize;
        terminalView.setTextSize(terminalTextSize);
    }

    private SessionFinishedListener sessionFinishedListener;

    public TerminalBackend(@NonNull TerminalView terminalView) {
        this.terminalView = terminalView;
        terminalView.setTextSize(getTerminalTextSize());
    }

    public interface SessionFinishedListener {
        void onSessionFinished(TerminalSession session);
    }

    public void setSessionFinishedListener(SessionFinishedListener listener) {
        this.sessionFinishedListener = listener;
    }

    @Override
    public void onTextChanged(TerminalSession changedSession) {
        terminalView.onScreenUpdated();
    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {

    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {
        if (confirmFinish) {
            if (sessionFinishedListener != null) {
                sessionFinishedListener.onSessionFinished(finishedSession);
            }
        } else {
            confirmFinish = true;
        }

    }

    @Override
    public void onCopyTextToClipboard(TerminalSession session, String text) {
        ClipboardUtils.copyText("GoTerminal", text);
    }

    @Override
    public void onPasteTextFromClipboard(TerminalSession session) {
        var clip = ClipboardUtils.getText().toString();
        if(!clip.trim().isEmpty() && terminalView.mEmulator != null){
            terminalView.mEmulator.paste(clip);
        }
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
        if (scale < 0.9f || scale > 1.1f) {
            boolean increase = scale > 1.0f;
            changeFont(increase);
            return 1.0f;
        }
        return scale;
    }

    private void changeFont(boolean increase) {
        terminalTextSize += (increase ? 1 : -1) * 2;
        terminalTextSize = Math.max(16, Math.min(terminalTextSize, 32));
        terminalView.setTextSize(terminalTextSize);
    }

    private void showSoftInput() {
        terminalView.requestFocus();
        KeyboardUtils.showSoftInput(terminalView);
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        showSoftInput();
    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return true;
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

    private boolean confirmFinish = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && !session.isRunning()) {
            if (sessionFinishedListener != null) {
                sessionFinishedListener.onSessionFinished(session);
            }
            return false;
        }
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
<<<<<<< HEAD
        if(terminalView.mEmulator != null){
            terminalView.setTerminalCursorBlinkerState(true,true);
            int textColor = MaterialColors.getColor(terminalView,com.google.android.material.R.attr.colorOnSurface);
            terminalView.mEmulator.mColors.mCurrentColors[256] = textColor;
            terminalView.mEmulator.mColors.mCurrentColors[257] = MaterialColors.getColor(terminalView,com.google.android.material.R.attr.colorSurface);
            terminalView.mEmulator.mColors.mCurrentColors[258] = textColor;
        }
=======
if (terminalView.mEmulator != null){
    terminalView.setTerminalCursorBlinkerState(true,true);
    int textColor = MaterialColors.getColor(terminalView,com.google.android.material.R.attr.colorOnSurface);
    terminalView.mEmulator.mColors.mCurrentColors[256] = textColor;
    terminalView.mEmulator.mColors.mCurrentColors[257] = MaterialColors.getColor(terminalView,com.google.android.material.R.attr.colorSurface);
    terminalView.mEmulator.mColors.mCurrentColors[258] = textColor;
}
>>>>>>> branch 'main' of https://github.com/omar-haidar/GoTerminal.git
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
