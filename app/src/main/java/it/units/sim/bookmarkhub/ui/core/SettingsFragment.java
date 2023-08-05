package it.units.sim.bookmarkhub.ui.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.repository.FirebaseAuthenticationHelper;
import it.units.sim.bookmarkhub.ui.authentication.AuthenticationActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.settings));
        }
        Preference logoutPreference = findPreference("logout");
        Objects.requireNonNull(logoutPreference).setOnPreferenceClickListener(preference -> {
            new Thread(() -> {
                String username = FirebaseAuthenticationHelper.getCurrentUserUsername();
                FirebaseAuthenticationHelper.signOut(
                        new FirebaseAuthenticationHelper.AuthenticationCallback() {
                            @Override
                            public void onSuccess() {
                                String msg = getString(R.string.logout_msg, username);
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show());
                                startActivity(new Intent(requireActivity(), AuthenticationActivity.class));
                            }

                            @Override
                            public void onFailure(int errorStringId) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), errorStringId, Toast.LENGTH_SHORT).show());
                            }
                        });
            }).start();
            return true;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dark_mode")) {
            boolean isDarkModeEnabled = sharedPreferences.getBoolean(key, false);
            updateDarkMode(isDarkModeEnabled);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode_enabled", isDarkModeEnabled);
            editor.apply();
        }
    }

    private void updateDarkMode(boolean isDarkModeEnabled) {
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        requireActivity().recreate();
    }

}
