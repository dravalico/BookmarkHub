package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class AddCategoryFragment extends Fragment {
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        EditText nameEditText = view.findViewById(R.id.categoryNameEditText);
        Button addCategoryButton = view.findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(v ->
                new Thread(() -> FirebaseCategoriesHelper.addNewCategory(
                        nameEditText.getText().toString(),
                        new FirebaseCategoriesHelper.CategoriesCallback() {
                            @Override
                            public void onSuccess(List<Category> category) {
                                AddCategoryFragment.this.clearViewAndOpenHomeFragment();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                )).start());
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> nameEditText.setText(""));
        return view;
    }

    public void clearViewAndOpenHomeFragment() {
        Toast.makeText(requireActivity(), "Category inserted successfully", Toast.LENGTH_SHORT).show();
        navController.navigate(AddCategoryFragmentDirections.actionCategoryToHomeFragment());
    }

}
