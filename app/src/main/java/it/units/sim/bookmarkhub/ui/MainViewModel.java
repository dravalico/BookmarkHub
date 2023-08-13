package it.units.sim.bookmarkhub.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categoriesList = new MutableLiveData<>(new ArrayList<>());

    public void fetchCategories() {
        new Thread(() -> FirebaseCategoryHelper.getCategoriesListOfCurrentUser(categoriesList)).start();
    }

    public MutableLiveData<List<Category>> categoriesList() {
        return categoriesList;
    }

    public List<String> getCategoriesNamesList() {
        return Objects.requireNonNull(categoriesList.getValue()).stream()
                .map(c -> c.name)
                .collect(Collectors.toList());
    }

}
