package it.units.sim.bookmarkhub.ui;

import android.app.Activity;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseAuthenticationHelper;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categoriesList = new MutableLiveData<>(new ArrayList<>());

    public void fetchCategories(Activity activity) {
        new Thread(() -> FirebaseCategoryHelper.getCategoriesListOfCurrentUser(
                new FirebaseCategoryHelper.CategoriesCallback() {
                    @Override
                    public void onSuccess(List<Category> categories) {
                        categoriesList.setValue(categories);
                    }

                    @Override
                    public void onError(int errorStringId) {
                        if (FirebaseAuthenticationHelper.isSomeoneLoggedIn()) {
                            Toast.makeText(activity, errorStringId, Toast.LENGTH_SHORT).show();
                        }
                    }
                })).start();
    }

    public MutableLiveData<List<Category>> categoriesList() {
        return categoriesList;
    }

    public List<Category> getCategoriesList() {
        return categoriesList.getValue();
    }

    public List<String> getCategoriesNamesList() {
        return Objects.requireNonNull(categoriesList.getValue()).stream()
                .map(c -> c.name)
                .collect(Collectors.toList());
    }

}
