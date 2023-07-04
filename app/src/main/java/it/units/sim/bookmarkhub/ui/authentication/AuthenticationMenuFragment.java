package it.units.sim.bookmarkhub.ui.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.persistence.DatabaseAuthListener;

public class AuthenticationMenuFragment extends Fragment implements DatabaseAuthListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_authentication_menu, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        view.findViewById(R.id.signInButton).setOnClickListener(v -> {
            NavDirections action = AuthenticationMenuFragmentDirections.actionToSignInFragment();
            navController.navigate(action);
        });
        view.findViewById(R.id.signUpButton).setOnClickListener(v -> {
            NavDirections action = AuthenticationMenuFragmentDirections.actionToSignUpFragment();
            navController.navigate(action);
        });
        return view;
    }

    @Override
    public void onSuccess(FirebaseUser user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }

}