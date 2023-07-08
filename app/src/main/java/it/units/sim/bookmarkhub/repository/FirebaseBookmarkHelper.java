package it.units.sim.bookmarkhub.repository;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.sim.bookmarkhub.model.Bookmark;

public class FirebaseBookmarkHelper {
    private static final String BOOKMARKS_COLLECTION_NAME = "bookmarks";

    public static void addNewBookmark(String name, String url, String category, BookmarkCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .add(new Bookmark(FirebaseAuth.getInstance().getUid(), name, url, category))
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface BookmarkCallback {
        void onSuccess(Bookmark Bookmark);

        void onError(String errorMessage);
    }

}
