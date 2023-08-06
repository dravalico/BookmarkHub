package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class HomeFragment extends Fragment implements MenuProvider {
    private CategoriesAdapter categoriesAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.home));
        }
        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByAscendingName(),
                        Category.class)
                .build();
        categoriesAdapter = new CategoriesAdapter(options, getParentFragmentManager());
        recyclerView.setAdapter(categoriesAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().addMenuProvider(this);
        categoriesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().removeMenuProvider(this);
        categoriesAdapter.stopListening();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.order_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_category) {
            AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
            addCategoryDialogFragment.show(getChildFragmentManager(), AddCategoryDialogFragment.TAG);
            return true;
        }
        if (menuItem.getItemId() == R.id.name_ascending) {
            categoriesAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByAscendingName(),
                            Category.class)
                    .build());
            return true;
        }
        if (menuItem.getItemId() == R.id.name_descending) {
            categoriesAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByDescendingName(),
                            Category.class)
                    .build());
            return true;
        }
        if (menuItem.getItemId() == R.id.date_ascending) {
            categoriesAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByAscendingDate(),
                            Category.class)
                    .build());
            return true;
        }
        if (menuItem.getItemId() == R.id.date_descending) {
            categoriesAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByDescendingDate(),
                            Category.class)
                    .build());
            return true;
        }
        return false;
    }

}