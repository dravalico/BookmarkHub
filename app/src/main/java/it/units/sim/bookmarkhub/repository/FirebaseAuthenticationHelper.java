package it.units.sim.bookmarkhub.repository;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class FirebaseAuthenticationHelper {

    private FirebaseAuthenticationHelper() {
    }

    public static void signUp(String username, String email, String password, String confirmPassword,
                              AuthenticationCallback callback) {
        try {
            checkSignUpFields(username, email, password, confirmPassword);
        } catch (IllegalArgumentException e) {
            callback.onFailure(e.getMessage());
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                updateUserName(firebaseUser, username, callback);
                callback.onSuccess();
            } else {
                callback.onFailure(Objects.requireNonNull(t.getException()).getMessage());
            }
        });
    }

    public static void signIn(String email, String password, AuthenticationCallback callback) {
        if (TextUtils.isEmpty(email)) {
            callback.onFailure("Email cannot be empty");
        }
        if (TextUtils.isEmpty(password)) {
            callback.onFailure("Password cannot be empty");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(Objects.requireNonNull(t.getException()).getMessage());
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

    private static void checkSignUpFields(String username, String email, String password,
                                          String confirmPassword) throws IllegalArgumentException {
        if (TextUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (TextUtils.isEmpty(email)) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            throw new IllegalArgumentException("Confirm password cannot be empty");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }

    private static void updateUserName(FirebaseUser firebaseUser, String username, AuthenticationCallback callback) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(t -> {
            if (!t.isSuccessful()) {
                callback.onFailure(Objects.requireNonNull(t.getException()).getMessage());
            }
        });
    }

    public interface AuthenticationCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }

}

