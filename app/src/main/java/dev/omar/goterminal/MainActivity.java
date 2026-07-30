package dev.omar.goterminal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.lifecycle.ViewModelProvider;

import com.termux.terminal.TerminalSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.terminal.TerminalBackend;
import dev.omar.goterminal.ui.base.EdgeToEdgeActivity;
import dev.omar.goterminal.ui.settings.SettingsActivity;
import dev.omar.goterminal.ui.sheet.ProgressSheetDialog;
import dev.omar.goterminal.utils.Environment;
import dev.omar.goterminal.utils.TerminalInstaller;
import dev.omar.goterminal.utils.UiUtils;

public class MainActivity extends EdgeToEdgeActivity {

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
    private TerminalBackend terminalBackend;

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
                .whenComplete((result, throwable) -> sheetDialog.dismiss());
        setupTerminalView();
        setupExtraKeysView();
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

        String prootBinary = TerminalInstaller.PROOT_FILE_PATH;
        String rootfs = TerminalInstaller.ROOTFS_PATH;
        String prefix = TerminalInstaller.PREFIX_PATH;
        String home = TerminalInstaller.HOME_PATH;

        List<String> args = new ArrayList<>();
        args.add(prootBinary);
        args.add("-i");
        args.add("0:0");
        args.add("-l");
        args.add("-r");
        args.add(rootfs);

        args.add("-b");
        args.add("/dev");
        args.add("-b");
        args.add("/proc");
        args.add("-b");
        args.add("/sys");
        args.add("-b");
        args.add("/system");
        args.add("-b");
        args.add(prefix + ":/data/data/com.termux/files/usr");
        args.add("-b");
        args.add(home + ":/data/data/com.termux/files/home");
        args.add("-b");
        args.add(TerminalInstaller.TMP_PATH + ":/data/data/com.termux/files/usr/tmp");

        args.add("-w");
        args.add(TerminalInstaller.HOME_PATH);


        Map<String, String> envMap = Environment.getEnvironment();
        envMap.remove("LD_PRELOAD");

        TerminalSession session =
                new TerminalSession(
                        prootBinary,
                        home,
                        args.toArray(new String[0]),
                        Environment.envToProps(envMap),
                        1000,
                        terminalBackend);

        binding.terminalView.attachSession(session);


    }

    private void setupExtraKeysView() {
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
}
