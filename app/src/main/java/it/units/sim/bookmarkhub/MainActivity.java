package it.units.sim.bookmarkhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.units.sim.bookmarkhub.persistence.DatabaseAuthListener;
import it.units.sim.bookmarkhub.persistence.FirebaseManager;
import it.units.sim.bookmarkhub.ui.authentication.AuthenticationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseManager.getCurrentUser() == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
        } else {
            startActivity(new Intent(this, TestActivity.class));
        }
    }

}