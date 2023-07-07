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
                              DatabaseEventListener databaseEventListener) throws IllegalArgumentException {
        checkSignUpFields(username, email, password, confirmPassword);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                updateUserName(firebaseUser, username, databaseEventListener);
                databaseEventListener.onSuccess();
            } else {
                databaseEventListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public static void signIn(String email, String password,
                              DatabaseEventListener databaseEventListener) throws IllegalArgumentException {
        if (TextUtils.isEmpty(email)) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseEventListener.onSuccess();
            } else {
                databaseEventListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
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

    private static void updateUserName(FirebaseUser firebaseUser, String username, DatabaseEventListener databaseEventListener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                databaseEventListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

}
