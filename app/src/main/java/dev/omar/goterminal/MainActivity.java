package dev.omar.goterminal;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dev.omar.goterminal.databinding.ActivityMainBinding;
import dev.omar.goterminal.ui.terminal.TerminalFragment;
import dev.omar.goterminal.terminal.TerminalBackend;
import dev.omar.goterminal.ui.adapter.SessionListAdapter;
import dev.omar.goterminal.ui.base.EdgeToEdgeActivity;
import dev.omar.goterminal.utils.ArchUtils;
import dev.omar.goterminal.utils.TerminalInstaller;
import dev.omar.goterminal.utils.UiUtils;
import com.termux.terminal.TerminalSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends EdgeToEdgeActivity implements SessionListAdapter.OnSessionClickListener {

    private final OnBackPressedCallback backCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (binding.drawerLayout != null && binding.drawerLayout.isOpen()) {
                        binding.drawerLayout.closeDrawers();
                    } else {
                        finish();
                    }
                }
            };
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private SessionListAdapter listAdapter;
    private final Map<String, TerminalFragment> fragmentMap = new HashMap<>();
    private String activeSessionHandle;

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
        
        listAdapter = new SessionListAdapter(this);
        binding.recyclerView.setAdapter(listAdapter);

        mainViewModel.getSessions().observe(this, this::syncFragmentsWithSessions);

        binding.imgAddSession.setOnClickListener(v -> mainViewModel.addNewSession());

        // Handle Installation before creating sessions
        checkInstallation();
    }

    private void checkInstallation() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = 
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Setting up environment")
                .setMessage("Please wait while we prepare the terminal system...")
                .setCancelable(false);
        
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        TerminalInstaller.installIfNeeded(this).thenAccept(result -> {
            runOnUiThread(() -> {
                dialog.dismiss();
                if (result.isSuccess()) {
                    // Create first session if empty after installation
                    if (mainViewModel.getSessions().getValue() == null || mainViewModel.getSessions().getValue().isEmpty()) {
                        mainViewModel.addNewSession();
                    }
                } else {
                    new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                            .setTitle("Installation Failed")
                            .setMessage(result.getMessage())
                            .setPositiveButton("Retry", (d, w) -> checkInstallation())
                            .setNegativeButton("Exit", (d, w) -> finish())
                            .show();
                }
            });
        });
    }

    private void syncFragmentsWithSessions(List<TerminalSession> sessions) {
        listAdapter.setSessions(sessions);
        
        // Remove fragments for closed sessions
        Map<String, TerminalFragment> toRemove = new HashMap<>(fragmentMap);
        for (TerminalSession session : sessions) {
            toRemove.remove(session.mHandle);
        }
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (Map.Entry<String, TerminalFragment> entry : toRemove.entrySet()) {
            ft.remove(entry.getValue());
            fragmentMap.remove(entry.getKey());
        }
        ft.commitNow();

        // Ensure we have an active session if list is not empty
        if (!sessions.isEmpty()) {
            if (activeSessionHandle == null || !containsSession(sessions, activeSessionHandle)) {
                switchToSession(sessions.get(0).mHandle);
            } else {
                // Just update UI selection
                updateListSelection(activeSessionHandle, sessions);
            }
        }
    }

    private boolean containsSession(List<TerminalSession> sessions, String handle) {
        for (TerminalSession s : sessions) {
            if (s.mHandle.equals(handle)) return true;
        }
        return false;
    }

    private void switchToSession(String handle) {
        if (handle.equals(activeSessionHandle) && fragmentMap.containsKey(handle)) return;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Hide current
        if (activeSessionHandle != null && fragmentMap.containsKey(activeSessionHandle)) {
            ft.hide(fragmentMap.get(activeSessionHandle));
        }

        // Show or Create new
        TerminalFragment fragment = fragmentMap.get(handle);
        if (fragment == null) {
            fragment = TerminalFragment.newInstance(handle);
            fragmentMap.put(handle, fragment);
            ft.add(R.id.fragment_container, fragment, handle);
        } else {
            ft.show(fragment);
        }

        activeSessionHandle = handle;
        ft.commitNow();
        
        updateListSelection(handle, mainViewModel.getSessions().getValue());
    }

    private void updateListSelection(String handle, List<TerminalSession> sessions) {
        if (sessions == null) return;
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).mHandle.equals(handle)) {
                listAdapter.setSelectedPosition(i);
                break;
            }
        }
    }

    @Override
    public void onSessionClick(int position) {
        List<TerminalSession> sessions = mainViewModel.getSessions().getValue();
        if (sessions != null && position < sessions.size()) {
            switchToSession(sessions.get(position).mHandle);
        }
        binding.drawerLayout.closeDrawers();
    }

    @Override
    public void onSessionDelete(TerminalSession session) {
        if (mainViewModel.getSessions().getValue() != null && mainViewModel.getSessions().getValue().size() <= 1) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Exit GoTerminal")
                    .setMessage("This is the last session. Do you want to exit the application?")
                    .setPositiveButton("Exit", (dialog, which) -> finish())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            mainViewModel.removeSession(session);
        }
    }

    private void setupLayoutInsets() {
        UiUtils.addSystemWindowInsetToPadding(
                binding.includeToolbar.appbar, true, true, true, false);
        UiUtils.addSystemWindowInsetToPadding(binding.layoutDrawer, true, true, true, true);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.includeToolbar.toolbar);
        binding.includeToolbar.toolbar.setSubtitle(ArchUtils.getArch());
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
}
