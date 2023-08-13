package it.units.sim.bookmarkhub.ui.core.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.ui.core.adapter.BookmarksAdapter;

public class CategoryEntriesFragment extends Fragment implements MenuProvider {
    private static final String ARG = "category_name";
    private String category;
    private BookmarksAdapter bookmarksAdapter;
    private ActionBar actionBar;

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
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(category);
            actionBar.setDisplayHomeAsUpEnabled(true);
            requireActivity().addMenuProvider(this);
        }
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
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        bookmarksAdapter.stopListening();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void addSwipeListenerToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    Bookmark bookmarkToDelete = bookmarksAdapter.getItem(swipedPosition);
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    String msg = bookmarkToDelete.name + " " + getString(R.string.deleted);
                    builder.setTitle(bookmarkToDelete.name)
                            .setMessage(R.string.confirm_bookmark_deletion)
                            .setPositiveButton(R.string.confirm_dialog, (dialog, which) ->
                                    new Thread(() -> FirebaseBookmarkHelper.deleteBookmark(
                                            bookmarkToDelete,
                                            new FirebaseBookmarkHelper.BookmarkCallback() {
                                                @Override
                                                public void onSuccess(List<Bookmark> bookmark) {
                                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onError(int errorStringId) {
                                                    Toast.makeText(requireContext(), errorStringId, Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            })).start())
                            .setNegativeButton(R.string.cancel_dialog, (dialog, which) ->
                                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition()))
                            .setOnCancelListener(dialog ->
                                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition()))
                            .show();
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    bookmarksAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    ModifyBookmarkDialogFragment dialogFragment =
                            ModifyBookmarkDialogFragment.newInstance(bookmarksAdapter.getItem(swipedPosition));
                    dialogFragment.show(getChildFragmentManager(), ModifyBookmarkDialogFragment.TAG);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // Not used
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_nav_host_fragment, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

}