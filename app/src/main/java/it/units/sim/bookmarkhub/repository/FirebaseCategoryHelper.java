package it.units.sim.bookmarkhub.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;

public class FirebaseCategoryHelper {
    private static final String CATEGORIES_COLLECTION_NAME = "categories";
    private static final String USER_ID_FIELD = "user_id";
    private static final String CATEGORY_NAME_FIELD = "category_name";
    private static final String CATEGORY_FOREIGN_KEY_FIELD = "category";

    private FirebaseCategoryHelper() {
    }

    public static void fetchCategories(MutableLiveData<List<Category>> categoriesLiveData) {
        FirebaseFirestore.getInstance()
                .collection(CATEGORIES_COLLECTION_NAME)
                .whereEqualTo(USER_ID_FIELD,
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderBy(CATEGORY_NAME_FIELD, Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d(FirebaseCategoryHelper.class.getName(), "Error while retrieve categories list");
                    }
                    if (value != null) {
                        List<Category> fetchedCategory = value.getDocuments()
                                .stream()
                                .map(s1 -> s1.toObject(Category.class))
                                .collect(Collectors.toList());
                        if (!fetchedCategory.equals(categoriesLiveData.getValue())) {
                            categoriesLiveData.postValue(fetchedCategory);
                        }
                    }
                });
    }

    public static void addNewCategoryIfNotAlreadySaved(String categoryName, FirebaseCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(CATEGORIES_COLLECTION_NAME)
                .whereEqualTo(CATEGORY_NAME_FIELD, categoryName)
                .whereEqualTo(USER_ID_FIELD, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            callback.onFailure(R.string.duplicated_category);
                        } else {
                            addNewCategory(categoryName, callback);
                        }
                    } else {
                        callback.onFailure(R.string.add_category_failure);
                    }
                });
    }

    public static void deleteCategoryAndContent(Category category, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<Void> transactionTask = db.runTransaction(transaction -> {
            DocumentReference categoryRef = db.collection(CATEGORIES_COLLECTION_NAME).document(category.id);
            transaction.delete(categoryRef);
            Query query = db.collection(FirebaseBookmarkHelper.BOOKMARKS_COLLECTION_NAME)
                    .whereEqualTo(CATEGORY_FOREIGN_KEY_FIELD, category.name);
            QuerySnapshot querySnapshot = null;
            try {
                querySnapshot = Tasks.await(query.get());
            } catch (ExecutionException | InterruptedException e) {
                callback.onFailure(R.string.delete_category_failure);
            }
            assert querySnapshot != null;
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                DocumentReference documentRef = document.getReference();
                transaction.delete(documentRef);
            }
            return null;
        });
        transactionTask
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(R.string.delete_category_failure));
    }

    public static void modifyCategoryName(Category categoryOld, Category categoryNew, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<Void> transactionTask = db.runTransaction(transaction -> {
            DocumentReference categoryRef = db.collection(CATEGORIES_COLLECTION_NAME).document(categoryNew.id);
            transaction.update(categoryRef, CATEGORY_NAME_FIELD, categoryNew.name);
            Query query = db.collection(FirebaseBookmarkHelper.BOOKMARKS_COLLECTION_NAME)
                    .whereEqualTo(CATEGORY_FOREIGN_KEY_FIELD, categoryOld.name);
            QuerySnapshot querySnapshot = null;
            try {
                querySnapshot = Tasks.await(query.get());
            } catch (ExecutionException | InterruptedException e) {
                callback.onFailure(R.string.modify_category_failure);
            }
            assert querySnapshot != null;
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                DocumentReference documentRef = document.getReference();
                transaction.update(documentRef, CATEGORY_FOREIGN_KEY_FIELD, categoryNew.name);
            }
            return null;
        });
        transactionTask
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(R.string.modify_category_failure));
    }

    private static void addNewCategory(String categoryName, FirebaseCallback callback) {
        if (categoryName.isEmpty()) {
            callback.onFailure(R.string.category_name_not_empty);
            return;
        }
        FirebaseFirestore.getInstance()
                .collection(CATEGORIES_COLLECTION_NAME)
                .add(new Category(FirebaseAuth.getInstance().getUid(), categoryName, new Date()))
                .addOnSuccessListener(r -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(R.string.add_category_failure));
    }

}
