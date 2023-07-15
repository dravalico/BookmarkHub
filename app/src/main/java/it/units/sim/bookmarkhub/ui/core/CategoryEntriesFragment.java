package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;

public class CategoryEntriesFragment extends Fragment {
    private static final String ARG = "category_name";
    private String category;
    private BookmarksAdapter bookmarksAdapter;

    public static CategoryEntriesFragment newInstance(String param1) {
        CategoryEntriesFragment fragment = new CategoryEntriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_category_entries, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.category_entries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<Bookmark> options = new FirestoreRecyclerOptions.Builder<Bookmark>()
                .setQuery(FirebaseBookmarkHelper.getQueryForBookmarksListOfCurrentUser(category), Bookmark.class)
                .build();
        bookmarksAdapter = new BookmarksAdapter(options, requireActivity());
        recyclerView.setAdapter(bookmarksAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bookmarksAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        bookmarksAdapter.stopListening();
    }

}