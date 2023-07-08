package it.units.sim.bookmarkhub.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.model.Category;

public class FirebaseCategoriesHelper {
    private static final String CATEGORIES_COLLECTION_NAME = "categories";

    public static Query getQueryForCategoriesListOfCurrentUser() {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(CATEGORIES_COLLECTION_NAME);
        return collectionRef.whereEqualTo("user_id",
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public static void getCategoriesListOfCurrentUser(CategoriesCallback callback) {
        getQueryForCategoriesListOfCurrentUser().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                callback.onError(e.getMessage());
            }
            assert snapshot != null;
            callback.onSuccess(
                    snapshot.getDocuments()
                            .stream()
                            .map(s -> s.toObject(Category.class))
                            .collect(Collectors.toList()));
        });
    }

    public interface CategoriesCallback {
        void onSuccess(List<Category> category);

        void onError(String errorMessage);
    }

}
