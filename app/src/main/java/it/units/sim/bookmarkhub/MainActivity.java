package it.units.sim.bookmarkhub;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import it.units.sim.bookmarkhub.repository.FirebaseAuthenticationHelper;
import it.units.sim.bookmarkhub.ui.authentication.AuthenticationActivity;
import it.units.sim.bookmarkhub.ui.core.fragment.SettingsFragment;
import it.units.sim.bookmarkhub.ui.core.viewmodel.CategoriesViewModel;

public class MainActivity extends AppCompatActivity {
    private NetworkChangeHandler networkChangeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode_enabled", false);
        SettingsFragment.updateDarkMode(isDarkModeEnabled);
        String language = sharedPreferences.getString("actual_language", "en");
        SettingsFragment.updateLanguage(language, this);
        networkChangeHandler = new NetworkChangeHandler(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeHandler, filter);
        if (!FirebaseAuthenticationHelper.isSomeoneLoggedIn()) {
            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setBackButtonBehaviour();
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
            NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            CategoriesViewModel categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
            categoriesViewModel.fetchCategories();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeHandler);
    }

    private void setBackButtonBehaviour() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("")
                        .setMessage(getString(R.string.close_app, getString(R.string.app_name)))
                        .setPositiveButton(R.string.confirm_dialog, (dialog, which) -> finishAffinity())
                        .setNegativeButton(R.string.cancel_dialog, null)
                        .show();
            }
        });
    }

}