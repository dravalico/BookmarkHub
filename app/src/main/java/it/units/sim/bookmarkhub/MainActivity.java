package it.units.sim.bookmarkhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import it.units.sim.bookmarkhub.persistence.FirebaseManager;
import it.units.sim.bookmarkhub.ui.authentication.AuthenticationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!FirebaseManager.isSomeoneLoggedIn()) {
            startActivity(new Intent(this, AuthenticationActivity.class));
        } else {
            startActivity(new Intent(this, TestActivity.class));
        }
    }

}