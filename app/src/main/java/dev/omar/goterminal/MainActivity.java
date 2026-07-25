package dev.omar.goterminal;

import android.os.Bundle;

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
        UiUtils.addSystemWindowInsetToPadding(binding.layoutMain,true,true,true,true);
        UiUtils.addSystemWindowInsetToPadding(binding.layoutDrawer,true,true,true,true);

    }
}