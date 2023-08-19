package it.units.sim.bookmarkhub.ui.authentication;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import it.units.sim.bookmarkhub.NetworkChangeHandler;
import it.units.sim.bookmarkhub.R;

public class AuthenticationActivity extends AppCompatActivity {
    private NetworkChangeHandler networkChangeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        networkChangeHandler = new NetworkChangeHandler(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeHandler, filter);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(AuthenticationActivity.this)
                        .setTitle("")
                        .setMessage(getString(R.string.close_app, getString(R.string.app_name)))
                        .setPositiveButton(R.string.confirm_dialog, (dialog, which) -> finishAffinity())
                        .setNegativeButton(R.string.cancel_dialog, null)
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeHandler);
    }

}