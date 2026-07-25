package dev.omar.goterminal;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import dev.omar.goterminal.ui.crash.CrashActivity;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        CrashActivity.initCrashHandler(this);

    }
}
