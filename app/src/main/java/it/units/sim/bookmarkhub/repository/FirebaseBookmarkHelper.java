package it.units.sim.bookmarkhub.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.sim.bookmarkhub.model.BookmarkEntity;

public class FirebaseBookmarkHelper {
    private static final String BOOKMARKS_COLLECTION_NAME = "bookmarks";

    public static void addNewBookmark(String name, String url, String category) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .add(new BookmarkEntity(FirebaseAuth.getInstance().getUid(), name, url, category))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public interface BookmarkCallback {
        void onSuccess(BookmarkEntity BookmarkEntity);

        void onError(Exception error);
    }

}
