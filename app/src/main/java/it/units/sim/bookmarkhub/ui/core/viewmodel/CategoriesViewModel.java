package it.units.sim.bookmarkhub.ui.core.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class CategoriesViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration listenerRegistration;

    public void fetchCategories() {
        listenerRegistration = FirebaseCategoryHelper.fetchCategories(categoriesLiveData);
    }

    public MutableLiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public List<String> getCategoriesNamesList() {
        return Objects.requireNonNull(categoriesLiveData.getValue()).stream()
                .map(c -> c.name)
                .collect(Collectors.toList());
    }

    public void stopFetch() {
        listenerRegistration.remove();
    }

}
