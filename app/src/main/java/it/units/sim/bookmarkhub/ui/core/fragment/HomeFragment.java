package it.units.sim.bookmarkhub.ui.core.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.ui.core.adapter.CategoriesAdapter;
import it.units.sim.bookmarkhub.ui.core.viewmodel.CategoriesViewModel;

public class HomeFragment extends Fragment implements MenuProvider {
    private static final String PREF_LAST_SORT_OPTION = "last_sort_option";
    private CategoriesViewModel categoriesViewModel;
    private List<Category> categories;
    private CategoriesAdapter categoriesAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private int lastOrderOption;
    private MenuItem lastSelectedItem;
    private TextView emptyHomeTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        lastOrderOption = sharedPreferences.getInt(PREF_LAST_SORT_OPTION, R.id.name_ascending);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        categories = new ArrayList<>();
        Log.d("DEB", this + "created");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.home));
        }
        recyclerView = view.findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
        categoriesAdapter = new CategoriesAdapter(categories, getParentFragmentManager(), navController);
        recyclerView.setAdapter(categoriesAdapter);
        FloatingActionButton addCategoryButton = view.findViewById(R.id.open_category_dialog_button);
        addCategoryButton.setOnClickListener(view1 -> {
            AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
            addCategoryDialogFragment.show(getChildFragmentManager(), AddCategoryDialogFragment.TAG);
        });
        emptyHomeTextView = view.findViewById(R.id.empty_home_text_view);
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
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEB", this + "destroyed");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoriesViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories1 -> {
            categories.clear();
            categories.addAll(categories1);
            if (categories.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyHomeTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyHomeTextView.setVisibility(View.GONE);
                sortCategories();
                categoriesAdapter.setCategoriesList(categories);
            }
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.sort_menu, menu);
        Menu submenu = menu.findItem(R.id.sort_menu).getSubMenu();
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