package dev.omar.goterminal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.termux.terminal.TerminalSession;

import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.terminal.service.TerminalService;
import dev.omar.goterminal.ui.adapter.SessionListAdapter;
import dev.omar.goterminal.ui.base.EdgeToEdgeActivity;
import dev.omar.goterminal.ui.settings.SettingsActivity;
import dev.omar.goterminal.ui.terminal.TerminalFragment;
import dev.omar.goterminal.utils.TerminalInstaller;
import dev.omar.goterminal.utils.UiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        /*binding.imgAddSession.setOnClickListener(v -> mainViewModel.addNewSession());*/
        binding.imgSettings.setOnClickListener(
                v -> SettingsActivity.openSettings(MainActivity.this));
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
}
