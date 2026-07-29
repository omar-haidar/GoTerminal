package dev.omar.goterminal;

import android.app.Application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import dev.omar.goterminal.ui.crash.CrashActivity;
import dev.omar.goterminal.utils.ArchUtils;
import org.lsposed.hiddenapibypass.HiddenApiBypass;

public class App extends Application {

    private static App sApp;

    @Override
    public void onCreate() {
        sApp = this;
        super.onCreate();
        Utils.init(this);
        CrashActivity.initCrashHandler(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        HiddenApiBypass.setHiddenApiExemptions("");
    }

    public static App get() {
        return sApp;
    }

    public static void exitApp() {
        ActivityUtils.finishAllActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void showAboutDialog(final Context context) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                .setTitle("About GoTerminal")
                .setMessage(
                        "GoTerminal v1.0\n\n"
                                + "A powerful terminal emulator for Android based on Termux technology, "
                                + "allowing you to run a Linux-like environment using PRoot.\n\n"
                                + "Developed by: Omar Haidar\n"
                                + "Build Architecture: "
                                + ArchUtils.getArch()
                                + "\n\n"
                                + "© 2026 GoTerminal Project")
                .setPositiveButton("Close", null)
                .setNeutralButton("Github", (d, i) -> openGithub(context))
                .setIcon(R.drawable.ic_comedy_mask)
                .show();
    }

    public static void openGithub(final Context context) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://github.com/omar-haidar/GoTerminal"));
            context.startActivity(i);
        } catch (Exception err) {
            Toast.makeText(
                            context,
                            "Failed to open github repo : " + err.getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }
}
