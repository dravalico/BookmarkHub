package it.units.sim.bookmarkhub.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import it.units.sim.bookmarkhub.model.CategoriesEntity;

public class FirebaseCategoriesHelper {
    private static final String CATEGORIES_COLLECTION_NAME = "categories";

    public static Query getQueryForCategoriesListOfCurrentUser() {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(CATEGORIES_COLLECTION_NAME);
        return collectionRef.whereEqualTo("owner_id",
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public static void getCategoriesListOfCurrentUser(CategoriesCallback callback) {
        getQueryForCategoriesListOfCurrentUser().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                callback.onError(e.getMessage());
            }
            assert snapshot != null;
            callback.onSuccess(snapshot.getDocuments().get(0).toObject(CategoriesEntity.class));
        });
    }

    public interface CategoriesCallback {
        void onSuccess(CategoriesEntity categoriesEntity);

        void onError(String errorMessage);
    }

}
