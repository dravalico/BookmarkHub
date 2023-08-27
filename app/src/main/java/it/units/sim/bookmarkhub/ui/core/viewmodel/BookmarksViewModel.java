package it.units.sim.bookmarkhub.ui.core.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;

public class BookmarksViewModel extends ViewModel {
    private final MutableLiveData<List<Bookmark>> bookmarksLiveData = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration listenerRegistration;

    public void fetchCategoryBookmarks(String categoryName) {
        listenerRegistration = FirebaseBookmarkHelper.fetchBookmarks(categoryName, bookmarksLiveData);
    }

    public MutableLiveData<List<Bookmark>> getBookmarksLiveData() {
        return bookmarksLiveData;
    }

    public void stopFetch() {
        listenerRegistration.remove();
    }

}
