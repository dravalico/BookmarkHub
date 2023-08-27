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
                              FirebaseCallback callback) {
        if (!isSignUpFieldsCompleted(username, email, password, confirmPassword, callback)) {
            return;
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                updateUserName(Objects.requireNonNull(firebaseUser), username, callback);
                callback.onSuccess();
            } else {
                callback.onFailure(R.string.sign_up_failure);
            }
        });
    }

    public static void signIn(String email, String password, FirebaseCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            callback.onFailure(R.string.email_password_not_empty);
            return;
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

    public static void signOut(FirebaseCallback callback) {
        try {
            FirebaseAuth.getInstance().signOut();
            callback.onSuccess();
        } catch (Exception e) {
            callback.onFailure(R.string.logout_failure);
        }
    }

    private static boolean isSignUpFieldsCompleted(String username, String email, String password, String confirmPassword,
                                                   FirebaseCallback callback) {
        if (TextUtils.isEmpty(username)) {
            callback.onFailure(R.string.username_not_empty);
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            callback.onFailure(R.string.email_password_not_empty);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            callback.onFailure(R.string.password_not_empty);
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            callback.onFailure(R.string.confirm_password_not_empty);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            callback.onFailure(R.string.password_match_error);
            return false;
        }
        return true;
    }

    private static void updateUserName(FirebaseUser firebaseUser, String username, FirebaseCallback callback) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailure(R.string.update_user_info_failure);
            }
        });
    }

}

