package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
    private EditText nameEditText;
    private Button addCategoryButton;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean isNameEditTextFilled = !nameEditText.getText().toString().isEmpty();
            addCategoryButton.setEnabled(isNameEditTextFilled);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add category");
        }
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        nameEditText = view.findViewById(R.id.category_name_edit_text);
        nameEditText.addTextChangedListener(textWatcher);
        addCategoryButton = view.findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(v ->
                new Thread(() -> FirebaseCategoriesHelper.addNewCategoryIfNotAlreadySaved(
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
        navController.navigate(AddCategoryFragmentDirections.actionAddCategoryFragmentToHomeFragment());
    }

}
