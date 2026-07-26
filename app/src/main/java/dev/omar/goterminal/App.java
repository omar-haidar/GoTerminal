package dev.omar.goterminal;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import dev.omar.goterminal.ui.crash.CrashActivity;

public class App extends Application {

    private static App sApp;
    @Override
    public void onCreate() {
        sApp = this;
        super.onCreate();
        Utils.init(this);
        CrashActivity.initCrashHandler(this);

    }

    public static App get(){
        return sApp;
    }
}
