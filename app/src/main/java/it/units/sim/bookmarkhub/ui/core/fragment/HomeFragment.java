package it.units.sim.bookmarkhub.ui.core.fragment;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.ui.MainViewModel;
import it.units.sim.bookmarkhub.ui.core.adapter.CategoriesAdapter;

public class HomeFragment extends Fragment implements MenuProvider {
    private static final String PREF_LAST_SORT_OPTION = "last_sort_option";
    private MainViewModel mainViewModel;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories;
    private SharedPreferences sharedPreferences;
    private int lastOrderOption;
    private MenuItem lastSelectedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        lastOrderOption = sharedPreferences.getInt(PREF_LAST_SORT_OPTION, R.id.name_ascending);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        categories = new ArrayList<>();
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
        categoriesAdapter = new CategoriesAdapter(categories, getParentFragmentManager());
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
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().removeMenuProvider(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.categoriesList().observe(getViewLifecycleOwner(), strings -> {
            categories.clear();
            categories.addAll(Objects.requireNonNull(mainViewModel.categoriesList().getValue()));
            sortCategories();
            categoriesAdapter.setCategoriesList(categories);
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.order_menu, menu);
        Menu submenu = menu.findItem(R.id.action_options).getSubMenu();
        if (submenu != null) {
            for (int i = 0; i < submenu.size(); i++) {
                MenuItem menuItem = submenu.getItem(i);
                if (menuItem.getItemId() == lastOrderOption) {
                    lastSelectedItem = menuItem;
                    menuItem.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.name_ascending) {
            lastOrderOption = R.id.name_ascending;
            setMenuItemChecked(menuItem);
            sortCategories();
            return true;
        }
        if (menuItem.getItemId() == R.id.name_descending) {
            lastOrderOption = R.id.name_descending;
            setMenuItemChecked(menuItem);
            sortCategories();
            return true;
        }
        if (menuItem.getItemId() == R.id.date_ascending) {
            lastOrderOption = R.id.date_ascending;
            setMenuItemChecked(menuItem);
            sortCategories();
            return true;
        }
        if (menuItem.getItemId() == R.id.date_descending) {
            lastOrderOption = R.id.date_descending;
            setMenuItemChecked(menuItem);
            sortCategories();
            return true;
        }
        return false;
    }

    private void setMenuItemChecked(MenuItem menuItem) {
        sharedPreferences.edit().putInt(PREF_LAST_SORT_OPTION, lastOrderOption).apply();
        if (lastSelectedItem != null) {
            lastSelectedItem.setChecked(false);
        }
        menuItem.setChecked(true);
        lastSelectedItem = menuItem;
    }

    private void sortCategories() {
        if (lastOrderOption == R.id.name_ascending) {
            categories.sort(Comparator.comparing(category -> category.name));
        }
        if (lastOrderOption == R.id.name_descending) {
            categories.sort(Comparator.comparing(category -> category.name));
            Collections.reverse(categories);
        }
        if (lastOrderOption == R.id.date_ascending) {
            categories.sort(Comparator.comparing(category -> category.creationDate));
        }
        if (lastOrderOption == R.id.date_descending) {
            categories.sort(Comparator.comparing(category -> category.creationDate));
            Collections.reverse(categories);
        }
        categoriesAdapter.setCategoriesList(categories);
    }

}