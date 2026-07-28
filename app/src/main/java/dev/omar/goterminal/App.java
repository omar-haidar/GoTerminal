package dev.omar.goterminal;

import android.app.Application;

import android.content.Context;
import com.blankj.utilcode.util.Utils;
import dev.omar.goterminal.ui.crash.CrashActivity;
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
    

    public static App get(){
        return sApp;
    }
}
