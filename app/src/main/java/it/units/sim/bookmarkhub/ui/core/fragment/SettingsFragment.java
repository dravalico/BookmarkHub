package it.units.sim.bookmarkhub.ui.core.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;
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
        setOnClickLogoutPreference(Objects.requireNonNull(logoutPreference));
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
            requireActivity().recreate();
            sharedPreferences.edit()
                    .putBoolean("dark_mode_enabled", isDarkModeEnabled)
                    .apply();
        }
        if (key.equals("language")) {
            String language = sharedPreferences.getString(key, "en");
            updateLanguage(language, requireContext());
            requireActivity().recreate();
            sharedPreferences.edit()
                    .putString("actual_language", language)
                    .apply();
        }
    }

    public static void updateLanguage(String language, Context context) {
        Locale locale = new Locale(language);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static void updateDarkMode(boolean isDarkModeEnabled) {
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setOnClickLogoutPreference(Preference logoutPreference) {
        logoutPreference.setOnPreferenceClickListener(preference -> {
            FirebaseAuthenticationHelper.signOut(
                    new FirebaseAuthenticationHelper.AuthenticationCallback() {
                        @Override
                        public void onSuccess() {
                            startActivity(new Intent(requireActivity(), AuthenticationActivity.class));
                        }

                        @Override
                        public void onFailure(int errorStringId) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), errorStringId, Toast.LENGTH_SHORT).show());
                        }
                    });
            return true;
        });
    }

}
