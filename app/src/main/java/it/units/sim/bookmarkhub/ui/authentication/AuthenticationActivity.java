package it.units.sim.bookmarkhub.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.units.sim.bookmarkhub.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}