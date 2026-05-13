package com.example.financeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.financeapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String[] currencies = {"ILS", "USD", "JOD", "EUR"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                currencies
        );
        binding.spinnerCurrency.setAdapter(currencyAdapter);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String savedCurrency = preferences.getString("currency", "ILS");
        boolean darkMode = preferences.getBoolean("dark_mode", false);

        binding.spinnerCurrency.setText(savedCurrency, false);
        binding.switchDarkMode.setChecked(darkMode);

        binding.btnSaveSettings.setOnClickListener(v -> {

            boolean isDark = binding.switchDarkMode.isChecked();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currency", binding.spinnerCurrency.getText().toString().trim());
            editor.putBoolean("dark_mode", isDark);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isDark
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

            recreate();
        });
    }
}