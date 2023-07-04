package it.units.sim.bookmarkhub.persistence;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class FirebaseManager {

    private FirebaseManager() {
    }

    public static void signUp(String username, String email, String password, String confirmPassword,
                              DatabaseAuthListener databaseAuthListener) throws IllegalArgumentException {
        checkSignUpFields(username, email, password, confirmPassword);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseAuthListener.onSuccess(firebaseAuth.getCurrentUser());
            } else {
                databaseAuthListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public static void signIn(String email, String password,
                              DatabaseAuthListener databaseAuthListener) throws IllegalArgumentException {
        if (TextUtils.isEmpty(email)) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseAuthListener.onSuccess(firebaseAuth.getCurrentUser());
            } else {
                databaseAuthListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
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

}

