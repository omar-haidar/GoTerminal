package dev.omar.goterminal.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import dev.omar.goterminal.databinding.ActivitySettingsBinding;
import dev.omar.goterminal.ui.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(binding.includeToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        binding.includeToolbar.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    public static void openSettings(Context context) {
        try {
            context.startActivity(new Intent(context, SettingsActivity.class));
        } catch (Exception e) {
            Toast.makeText(context, "Failed to open settings activity : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
