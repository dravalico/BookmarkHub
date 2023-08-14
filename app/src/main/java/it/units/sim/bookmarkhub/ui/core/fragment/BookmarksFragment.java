package it.units.sim.bookmarkhub.ui.core.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.ui.core.adapter.BookmarksAdapter;
import it.units.sim.bookmarkhub.ui.core.viewmodel.BookmarksViewModel;

public class BookmarksFragment extends Fragment implements MenuProvider {
    private static final String ARG = "category_name";
    private String categoryName;
    private BookmarksViewModel bookmarksViewModel;
    private BookmarksAdapter bookmarksAdapter;
    private RecyclerView recyclerView;
    private ActionBar actionBar;
    private TextView emptyCategoryTextView;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString(ARG);
        }
        bookmarksViewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        Log.d("bok", categoryName);
        bookmarksViewModel.fetchCategoryBookmarks(categoryName);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(categoryName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            requireActivity().addMenuProvider(this);
        }
        recyclerView = view.findViewById(R.id.bookmarks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bookmarksAdapter = new BookmarksAdapter(bookmarksViewModel.getBookmarksLiveData().getValue(), requireActivity());
        recyclerView.setAdapter(bookmarksAdapter);
        addSwipeListenerToRecyclerView();
        emptyCategoryTextView = view.findViewById(R.id.empty_category_text_view);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookmarksViewModel.getBookmarksLiveData().observe(getViewLifecycleOwner(), bookmarks -> {
                    if (bookmarks.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyCategoryTextView.setVisibility(View.VISIBLE);
                    } else {
                        emptyCategoryTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        bookmarksAdapter.setBookmarksList(bookmarks);
                    }
                }
        );
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // Not used
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            navController.navigate(R.id.action_bookmarksFragment_to_homeFragment);
            return true;
        }
        return false;
    }

    private void addSwipeListenerToRecyclerView() {
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

}