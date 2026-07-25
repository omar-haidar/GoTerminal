package dev.omar.goterminal;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;

import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.ui.base.EdgeToEdgeActivity;
import dev.omar.goterminal.utils.UiUtils;

public class MainActivity extends EdgeToEdgeActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupLayoutInsets();
        setupToolbar();
    }

    private void setupLayoutInsets() {
        UiUtils.addSystemWindowInsetToPadding(binding.layoutMain, true, true, true, true);
        UiUtils.addSystemWindowInsetToPadding(binding.layoutDrawer, true, true, true, true);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.includeToolbar.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.includeToolbar.toolbar, R.string.app_name, R.string.app_name);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


}