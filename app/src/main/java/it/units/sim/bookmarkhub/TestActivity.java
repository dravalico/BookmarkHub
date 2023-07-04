package it.units.sim.bookmarkhub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import it.units.sim.bookmarkhub.persistence.FirebaseManager;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView textView = findViewById(R.id.text_view_id);
        textView.setText(FirebaseManager.getCurrentUser().getDisplayName());
    }

}