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
import androidx.navigation.Navigation;

import it.units.sim.bookmarkhub.MainActivity;
import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.repository.FirebaseAuthenticationHelper;
import it.units.sim.bookmarkhub.repository.FirebaseCallback;

public class SignInFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        EditText emailEditText = view.findViewById(R.id.email_edit_text);
        EditText passwordEditText = view.findViewById(R.id.password_edit_text);
        view.findViewById(R.id.sign_in_button).setOnClickListener(view1 -> signIn(emailEditText, passwordEditText));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        view.findViewById(R.id.sign_up_redirect_button).setOnClickListener(view1 -> {
            NavDirections action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment();
            navController.navigate(action);
        });
        return view;
    }

    private void signIn(EditText emailEditText, EditText passwordEditText) {
        FirebaseAuthenticationHelper.signIn(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                new FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().finish();
                    }

                    @Override
                    public void onFailure(int errorStringId) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireActivity(), errorStringId, Toast.LENGTH_SHORT).show());
                    }
                });
    }

}