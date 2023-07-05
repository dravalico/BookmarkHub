package it.units.sim.bookmarkhub.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.units.sim.bookmarkhub.MainActivity;
import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.persistence.DatabaseAuthListener;
import it.units.sim.bookmarkhub.persistence.FirebaseManager;

public class SignInFragment extends Fragment implements DatabaseAuthListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        view.findViewById(R.id.signInButton).setOnClickListener(v -> {
            EditText emailEditText = view.findViewById(R.id.emailEditText);
            EditText passwordEditText = view.findViewById(R.id.passwordEditText);
            try {
                FirebaseManager.signIn(emailEditText.getText().toString(), passwordEditText.getText().toString(), this);
            } catch (IllegalArgumentException e) {
                onFailure(e.getMessage());
            }
        });
        view.findViewById(R.id.signUpRedirectTextView).setOnClickListener(v -> {
            NavDirections action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment();
            navController.navigate(action);
        });
        return view;
    }

    @Override
    public void onSuccess(FirebaseUser user) {
        startActivity(new Intent(requireActivity(), MainActivity.class));
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

}