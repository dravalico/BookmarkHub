package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

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
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(category);
        }
        View view = inflater.inflate(R.layout.fragment_category_entries, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.category_entries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<Bookmark> options = new FirestoreRecyclerOptions.Builder<Bookmark>()
                .setQuery(FirebaseBookmarkHelper.getQueryForBookmarksListOfCurrentUser(category), Bookmark.class)
                .build();
        bookmarksAdapter = new BookmarksAdapter(options, requireActivity());
        recyclerView.setAdapter(bookmarksAdapter);
        addSwipeListenerToRecyclerView(recyclerView);
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

    private void addSwipeListenerToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("")
                            .setMessage("Are you sure you want to delete the bookmark?")
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                int swipedPosition = viewHolder.getAdapterPosition();
                                Bookmark bookmarkToDelete = bookmarksAdapter.getItem(swipedPosition);
                                new Thread(() -> FirebaseBookmarkHelper.deleteBookmark(bookmarkToDelete,
                                        new FirebaseBookmarkHelper.BookmarkCallback() {
                                            @Override
                                            public void onSuccess(List<Bookmark> bookmark) {
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        })).start();
                            })
                            .setNegativeButton("Cancel", (dialog, which) ->
                                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition()))
                            .setOnCancelListener(dialog ->
                                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition()))
                            .show();
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}