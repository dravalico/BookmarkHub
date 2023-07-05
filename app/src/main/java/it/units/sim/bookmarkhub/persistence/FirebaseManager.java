package it.units.sim.bookmarkhub.persistence;

import static android.content.ContentValues.TAG;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

import it.units.sim.bookmarkhub.business.Bookmark;

public class FirebaseManager {
    private static final String BOOKMARKS_COLLECTION_NAME = "bookmarks";
    private static final String CATEGORIES_COLLECTION_NAME = "categories";

    private FirebaseManager() {
    }

    public static void signUp(String username, String email, String password, String confirmPassword,
                              DatabaseAuthListener databaseAuthListener) throws IllegalArgumentException {
        checkSignUpFields(username, email, password, confirmPassword);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                updateUserName(firebaseUser, username, databaseAuthListener);
                databaseAuthListener.onSuccess(firebaseUser);
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

    public static boolean isSomeoneLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getCurrentUserUsername() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void retrieveCategoriesListOfCurrentUser(DatabaseDataListener<List<String>> databaseDataListener) {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(CATEGORIES_COLLECTION_NAME);
        Query query = collectionRef.whereEqualTo("owner_id",
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.get().addOnSuccessListener(q -> {
                    List<String> categories = (List<String>) q.getDocuments().get(0).get("name");
                    databaseDataListener.onSuccess(categories);
                })
                .addOnFailureListener(e -> databaseDataListener.onFailure(e.getMessage()));
    }

    public static void addNewBookmark(String name, String url, String category) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .add(new Bookmark(FirebaseAuth.getInstance().getUid(), name, url, category))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
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

    private static void updateUserName(FirebaseUser firebaseUser, String username,
                                       DatabaseAuthListener databaseAuthListener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                databaseAuthListener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

}

