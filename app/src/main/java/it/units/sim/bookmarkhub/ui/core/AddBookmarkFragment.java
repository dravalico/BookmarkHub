package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class AddBookmarkFragment extends Fragment {
    private NavController navController;
    private EditText nameEditText;
    private EditText urlEditText;
    private EditText dataEditText;
    private ArrayAdapter<String> spinnerAdapter;
    private Button addBookmarkButton;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int maxNameLength = 15;
            if (nameEditText.getText().toString().length() > maxNameLength) {
                nameEditText.setText(s.subSequence(0, maxNameLength));
                nameEditText.setSelection(maxNameLength);
            }
            int maxDataLength = 30;
            if (dataEditText.getText().toString().length() > maxDataLength) {
                dataEditText.setText(s.subSequence(0, maxDataLength));
                dataEditText.setSelection(maxDataLength);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean isNameEditTextFilled = !nameEditText.getText().toString().isEmpty();
            boolean isUrlEditTextFilled = !urlEditText.getText().toString().isEmpty();
            boolean isSpinnerAdapterFilled = !spinnerAdapter.isEmpty();
            addBookmarkButton.setEnabled(isNameEditTextFilled && isUrlEditTextFilled && isSpinnerAdapterFilled);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        Spinner spinner = view.findViewById(R.id.bookmark_category_spinner);
        spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        nameEditText = view.findViewById(R.id.bookmark_name_edit_text);
        nameEditText.addTextChangedListener(textWatcher);
        urlEditText = view.findViewById(R.id.bookmark_url_edit_text);
        urlEditText.addTextChangedListener(textWatcher);
        dataEditText = view.findViewById(R.id.bookmark_data_edit_text);
        dataEditText.addTextChangedListener(textWatcher);
        new Thread(() -> FirebaseCategoriesHelper.getCategoriesListOfCurrentUser(
                new FirebaseCategoriesHelper.CategoriesCallback() {
                    @Override
                    public void onSuccess(List<Category> category) {
                        AddBookmarkFragment.this.setAdapterSpinnerValues(
                                category.stream()
                                        .map(c -> c.name)
                                        .collect(Collectors.toList()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                })).start();
        addBookmarkButton = view.findViewById(R.id.add_bookmark_button);
        addBookmarkButton.setOnClickListener(v -> {
            if (URLUtil.isValidUrl(urlEditText.getText().toString())) {
                new Thread(() ->
                        FirebaseBookmarkHelper.addNewBookmark(
                                nameEditText.getText().toString(),
                                urlEditText.getText().toString(),
                                dataEditText.getText().toString(),
                                spinner.getSelectedItem().toString(),
                                new FirebaseBookmarkHelper.BookmarkCallback() {
                                    @Override
                                    public void onSuccess(List<Bookmark> bookmark) {
                                        AddBookmarkFragment.this.clearViewAndOpenHomeFragment();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                })).start();
            } else {
                Toast.makeText(requireActivity(), "The URL is not valid", Toast.LENGTH_SHORT).show();
            }
        });
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> resetEditTextViews());
        return view;
    }

    public void clearViewAndOpenHomeFragment() {
        resetEditTextViews();
        Toast.makeText(requireActivity(), "Bookmark inserted successfully", Toast.LENGTH_SHORT).show();
        navController.navigate(AddBookmarkFragmentDirections.actionAddBookmarkFragmentToHomeFragment());
    }

    public void setAdapterSpinnerValues(List<String> values) {
        spinnerAdapter.clear();
        spinnerAdapter.addAll(values);
    }

    private void resetEditTextViews() {
        nameEditText.setText("");
        urlEditText.setText("");
        dataEditText.setText("");
    }

}