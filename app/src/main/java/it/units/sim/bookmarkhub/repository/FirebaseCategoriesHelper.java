package it.units.sim.bookmarkhub.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
        getQueryForCategoriesListOfCurrentUser().addSnapshotListener((s, e) -> {
            if (e != null) {
                callback.onError(e.getMessage());
            }
            assert s != null;
            callback.onSuccess(
                    s.getDocuments()
                            .stream()
                            .map(s1 -> s1.toObject(Category.class))
                            .collect(Collectors.toList()));
        });
    }

    public static void addNewCategoryIfNotAlreadySaved(String categoryName, CategoriesCallback callback) {
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(CATEGORIES_COLLECTION_NAME);
        Task<QuerySnapshot> queryTask = collectionRef
                .whereEqualTo("category_name", categoryName)
                .whereEqualTo("user_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get();
        queryTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    callback.onError("This category is already present in the database");
                } else {
                    addNewCategory(categoryName, callback);
                }
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private static void addNewCategory(String categoryName, CategoriesCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(CATEGORIES_COLLECTION_NAME)
                .add(new Category(FirebaseAuth.getInstance().getUid(), categoryName))
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface CategoriesCallback {
        void onSuccess(List<Category> category);

        void onError(String errorMessage);
    }

}
