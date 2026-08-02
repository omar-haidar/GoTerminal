package dev.omar.goterminal;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.lifecycle.ViewModelProvider;

import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;

import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.terminal.TerminalBackend;
import dev.omar.goterminal.terminal.service.TerminalService;
import dev.omar.goterminal.terminal.service.TerminalServiceAction;
import dev.omar.goterminal.terminal.service.TerminalServiceBinder;
import dev.omar.goterminal.ui.base.EdgeToEdgeActivity;
import dev.omar.goterminal.ui.settings.SettingsActivity;
import dev.omar.goterminal.ui.sheet.ProgressSheetDialog;
import dev.omar.goterminal.utils.Environment;
import dev.omar.goterminal.utils.TerminalInstaller;
import dev.omar.goterminal.utils.UiUtils;

public class MainActivity extends EdgeToEdgeActivity implements ServiceConnection {

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

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private TerminalBackend terminalBackend;

    private TerminalService terminalService;

    private boolean isBound = false;

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
        ProgressSheetDialog sheetDialog =
                new ProgressSheetDialog.Builder()
                        .setTitle("Setup")
                        .setMessage("Installing tools ...")
                        .setCancelable(false)
                        .show(getSupportFragmentManager(), "installing-tools");
        TerminalInstaller.installIfNeeded(this)
                .whenComplete((result, throwable) -> {
                    runOnUiThread(() -> {
                        setupTerminalView();
                    });
                    sheetDialog.dismiss();
                });


        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        /*binding.imgAddSession.setOnClickListener(v -> mainViewModel.addNewSession());*/
        binding.imgSettings.setOnClickListener(
                v -> SettingsActivity.openSettings(MainActivity.this));
    }

    private void setupTerminalView() {
        terminalBackend = new TerminalBackend(binding.terminalView);
        terminalBackend.setSessionFinishedListener(session -> finish());
        binding.terminalView.setTerminalViewClient(terminalBackend);
        binding.terminalView.setKeepScreenOn(true);


        TerminalSession session =
                new TerminalSession(
                        "/system/bin/sh",
                        TerminalInstaller.ROOTFS_PATH,
                        new String[]{"-c", TerminalInstaller.INIT_HOST_FILE_PATH},
                        Environment.envToProps(Environment.getEnvironment()),
                        TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
                        terminalBackend);

        binding.terminalView.attachSession(session);

    }


    private void setupLayoutInsets() {
        UiUtils.addSystemWindowInsetToPadding(
                binding.includeToolbar.appbar, true, true, true, false);
        UiUtils.addSystemWindowAndImeInsetToPadding(binding.navView, true, true, false, false);
        UiUtils.addSystemWindowAndImeInsetToPadding(binding.layoutMain, true, false, true, true);
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

    private boolean bindService() {
        Intent intent = new Intent(MainActivity.this, TerminalService.class);
        intent.setAction(TerminalServiceAction.ACTION_BIND);
        startService(intent);
        return bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        terminalService = ((TerminalServiceBinder) iBinder).getService();
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
        Log.i(TAG, "onServiceDisconnected");
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }
}
