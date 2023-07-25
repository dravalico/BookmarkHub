package it.units.sim.bookmarkhub.repository;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;

public class FirebaseBookmarkHelper {
    public static final String BOOKMARKS_COLLECTION_NAME = "bookmarks";

    public static void addNewBookmark(String name, String url, String data, String category, BookmarkCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .add(new Bookmark(FirebaseAuth.getInstance().getUid(), name, url, data, category))
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(R.string.add_bookmark_failure));
    }

    public static Query getQueryForBookmarksListOfCurrentUser(String category) {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(BOOKMARKS_COLLECTION_NAME);
        return collectionRef.whereEqualTo("category", category);
    }

    public static void deleteBookmark(Bookmark bookmark, BookmarkCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .document(bookmark.id)
                .delete()
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(R.string.delete_bookmark_failure));
    }

    public static void modifyBookmark(Bookmark bookmark, BookmarkCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(BOOKMARKS_COLLECTION_NAME)
                .document(bookmark.id)
                .set(bookmark)
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(R.string.modify_bookmark_failure));
    }

    public interface BookmarkCallback {
        void onSuccess(List<Bookmark> bookmark);

        void onError(int errorStringId);
    }

}
