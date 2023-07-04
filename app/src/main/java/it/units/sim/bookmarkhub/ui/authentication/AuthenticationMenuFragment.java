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

import java.util.Objects;

import it.units.sim.bookmarkhub.R;

public class AuthenticationMenuFragment extends Fragment {

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

}