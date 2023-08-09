package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class HomeFragment extends Fragment implements MenuProvider {
    private CategoriesAdapter categoriesAdapter;
    private static final String PREF_LAST_SORT_OPTION = "last_sort_option";
    private SharedPreferences sharedPreferences;
    private int lastSortOption;
    private MenuItem lastSelectedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        lastSortOption = sharedPreferences.getInt(PREF_LAST_SORT_OPTION, R.id.name_ascending);
    }

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
        FirestoreRecyclerOptions<Category> options = getQueryBasedOnSelectedSortingOption(lastSortOption);
        categoriesAdapter = new CategoriesAdapter(options, getParentFragmentManager());
        recyclerView.setAdapter(categoriesAdapter);
        FloatingActionButton addCategoryButton = view.findViewById(R.id.open_category_dialog_button);
        addCategoryButton.setOnClickListener(view1 -> {
            AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
            addCategoryDialogFragment.show(getChildFragmentManager(), AddCategoryDialogFragment.TAG);
        });
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
        Menu submenu = menu.findItem(R.id.action_options).getSubMenu();
        if (submenu != null) {
            for (int i = 0; i < submenu.size(); i++) {
                MenuItem menuItem = submenu.getItem(i);
                if (menuItem.getItemId() == lastSortOption) {
                    lastSelectedItem = menuItem;
                    menuItem.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.name_ascending) {
            lastSortOption = R.id.name_ascending;
            setMenuItemChecked(menuItem);
            categoriesAdapter.updateOptions(getQueryBasedOnSelectedSortingOption(R.id.name_ascending));
            return true;
        }
        if (menuItem.getItemId() == R.id.name_descending) {
            lastSortOption = R.id.name_descending;
            setMenuItemChecked(menuItem);
            categoriesAdapter.updateOptions(getQueryBasedOnSelectedSortingOption(R.id.name_descending));
            return true;
        }
        if (menuItem.getItemId() == R.id.date_ascending) {
            lastSortOption = R.id.date_ascending;
            setMenuItemChecked(menuItem);
            categoriesAdapter.updateOptions(getQueryBasedOnSelectedSortingOption(R.id.date_ascending));
            return true;
        }
        if (menuItem.getItemId() == R.id.date_descending) {
            lastSortOption = R.id.date_descending;
            setMenuItemChecked(menuItem);
            categoriesAdapter.updateOptions(getQueryBasedOnSelectedSortingOption(R.id.date_descending));
            return true;
        }
        return false;
    }

    private static FirestoreRecyclerOptions<Category> getQueryBasedOnSelectedSortingOption(int sortOption) {
        if (sortOption == R.id.name_descending) {
            return new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByDescendingName(),
                            Category.class)
                    .build();
        }
        if (sortOption == R.id.date_ascending) {
            return new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByAscendingDate(),
                            Category.class)
                    .build();
        }
        if (sortOption == R.id.date_descending) {
            return new FirestoreRecyclerOptions.Builder<Category>()
                    .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByDescendingDate(),
                            Category.class)
                    .build();
        }
        return new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseCategoryHelper.getQueryForCategoriesListOfCurrentUserOrderedByAscendingName(),
                        Category.class)
                .build();
    }

    private void setMenuItemChecked(MenuItem menuItem) {
        sharedPreferences.edit().putInt(PREF_LAST_SORT_OPTION, lastSortOption).apply();
        if (lastSelectedItem != null) {
            lastSelectedItem.setChecked(false);
        }
        menuItem.setChecked(true);
        lastSelectedItem = menuItem;
    }

}