package it.units.sim.bookmarkhub.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import it.units.sim.bookmarkhub.model.CategoriesEntity;

public class FirebaseCategoriesHelper {
    private static final String CATEGORIES_COLLECTION_NAME = "categories";

    public static void getCategoriesListOfCurrentUser(CategoriesCallback callback) {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(CATEGORIES_COLLECTION_NAME);
        Query query = collectionRef.whereEqualTo("owner_id",
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.get()
                .addOnSuccessListener(q ->
                        callback.onSuccess(q.getDocuments().get(0).toObject(CategoriesEntity.class)))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface CategoriesCallback {
        void onSuccess(CategoriesEntity categoriesEntity);

        void onError(String errorMessage);
    }

}
