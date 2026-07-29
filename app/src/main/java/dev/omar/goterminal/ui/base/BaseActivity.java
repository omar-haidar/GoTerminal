package dev.omar.goterminal.ui.base;

import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void showToast(final CharSequence message, final int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(this, message, duration).show();
        } else {
            runOnUiThread(() -> {
                Toast.makeText(this, message, duration).show();
            });
        }
    }
}
