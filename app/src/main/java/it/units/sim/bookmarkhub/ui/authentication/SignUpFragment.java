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

import java.util.Objects;

import it.units.sim.bookmarkhub.MainActivity;
import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.repository.FirebaseAuthenticationHelper;

public class SignUpFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        EditText emailEditText = view.findViewById(R.id.email_edit_text);
        EditText passwordEditText = view.findViewById(R.id.password_edit_text);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        view.findViewById(R.id.sign_up_button).setOnClickListener(view1 ->
                signUp(usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText));
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        view.findViewById(R.id.sign_in_redirect_button).setOnClickListener(view1 -> {
            NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment();
            navController.navigate(action);
        });
        return view;
    }

    private void signUp(EditText usernameEditText, EditText emailEditText, EditText passwordEditText, EditText confirmPasswordEditText) {
        new Thread(() -> FirebaseAuthenticationHelper.signUp(
                usernameEditText.getText().toString(),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                confirmPasswordEditText.getText().toString(),
                new FirebaseAuthenticationHelper.AuthenticationCallback() {
                    @Override
                    public void onSuccess() {
                        String msg = getString(R.string.sign_up_msg, usernameEditText.getText().toString());
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().finish();
                    }

                    @Override
                    public void onFailure(int errorStringId) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireActivity(), errorStringId, Toast.LENGTH_SHORT).show());
                    }
                })).start();
    }

}