package dev.omar.goterminal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.KeyboardUtils;

import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.ui.base.BaseTerminalActivity;
import dev.omar.goterminal.ui.settings.SettingsActivity;
import dev.omar.goterminal.utils.UiUtils;

public class MainActivity extends BaseTerminalActivity {

    private final OnBackPressedCallback backCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (binding.drawerLayout.isOpen()) {
                        binding.drawerLayout.closeDrawers();
                    } else {
                        finish();
                    }
                }
            };
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private int terminalTextSize = 16;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupLayoutInsets();
        setupToolbar();
        initializeLogic();
    }

    private void initializeLogic() {
        setupExtraKeysView();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        /*binding.imgAddSession.setOnClickListener(v -> mainViewModel.addNewSession());*/
        binding.imgSettings.setOnClickListener(
                v -> SettingsActivity.openSettings(MainActivity.this));
        setupTerminalView();
    }

    private void setupTerminalView() {
        binding.terminalView.setTerminalViewClient(MainActivity.this);
        binding.terminalView.setTextSize(terminalTextSize);
        binding.terminalView.setKeepScreenOn(true);
    }

    private void setupExtraKeysView() {

    }

    private void setupLayoutInsets() {
        UiUtils.addSystemWindowInsetToPadding(
                binding.includeToolbar.appbar, true, true, true, false);
        UiUtils.addSystemWindowInsetToPadding(binding.navView, true, true, false, false);
        UiUtils.addSystemWindowInsetToPadding(binding.layoutMain, true, false, true, true);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.includeToolbar.toolbar);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        binding.drawerLayout,
                        binding.includeToolbar.toolbar,
                        R.string.app_name,
                        R.string.app_name);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getOnBackPressedDispatcher().addCallback(this, backCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings -> SettingsActivity.openSettings(MainActivity.this);
            case R.id.menu_item_about -> App.showAboutDialog(MainActivity.this);
            case R.id.menu_item_exit -> App.exitApp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        showSoftInput();
    }

    private void showSoftInput(){
        binding.terminalView.requestFocus();
        KeyboardUtils.showSoftInput(binding.terminalView);
    }

    @Override
    public float onScale(float scale) {
        if (scale < 0.9f || scale > 1.1f){
            boolean increase = scale > 1.0f;
            changeFont(increase);
            return 1.0f;
        }
        return scale;
    }

    private void changeFont(boolean increase) {
        terminalTextSize += (increase ? 1 : -1) * 2;
        terminalTextSize = Math.max(16,Math.min(terminalTextSize,32));
        binding.terminalView.setTextSize(terminalTextSize);
    }

}
