package it.units.sim.bookmarkhub.repository;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import it.units.sim.bookmarkhub.R;

public class FirebaseAuthenticationHelper {

    private FirebaseAuthenticationHelper() {
    }

    public static void signUp(String username, String email, String password, String confirmPassword,
                              AuthenticationCallback callback) {
        checkSignUpFields(username, email, password, confirmPassword, callback);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                updateUserName(firebaseUser, username, callback); // TODO check error handling
                callback.onSuccess();
            } else {
                callback.onFailure(R.string.sign_up_failure);
            }
        });
    }

    public static void signIn(String email, String password, AuthenticationCallback callback) {
        if (TextUtils.isEmpty(email)) {
            callback.onFailure(R.string.email_not_empty);
        }
        if (TextUtils.isEmpty(password)) {
            callback.onFailure(R.string.password_not_empty);
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(R.string.sign_in_failure);
            }
        });
    }

    public static boolean isSomeoneLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getCurrentUserUsername() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private static void checkSignUpFields(String username, String email, String password, String confirmPassword,
                                          AuthenticationCallback callback) {
        if (TextUtils.isEmpty(username)) {
            callback.onFailure(R.string.username_not_empty);
        }
        if (TextUtils.isEmpty(email)) {
            callback.onFailure(R.string.email_not_empty);
        }
        if (TextUtils.isEmpty(password)) {
            callback.onFailure(R.string.password_not_empty);
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            callback.onFailure(R.string.confirm_password_not_empty);
        }
        if (!password.equals(confirmPassword)) {
            callback.onFailure(R.string.password_match_error);
        }
    }

    private static void updateUserName(FirebaseUser firebaseUser, String username, AuthenticationCallback callback) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailure(R.string.update_user_info_failure);
            }
        });
    }

    public interface AuthenticationCallback {
        void onSuccess();

        void onFailure(int errorStringId);
    }

}

