package it.units.sim.bookmarkhub.ui.core.fragment;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.ui.core.viewmodel.CategoriesViewModel;

public class AddBookmarkFragment extends Fragment {
    private NavController navController;
    private EditText nameEditText;
    private EditText urlEditText;
    private EditText dataEditText;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner;
    private Button addBookmarkButton;
    private CategoriesViewModel categoriesViewModel;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int maxNameLength = 30;
            if (nameEditText.getText().toString().length() > maxNameLength) {
                nameEditText.setText(s.subSequence(0, maxNameLength));
                nameEditText.setSelection(maxNameLength);
            }
            int maxDataLength = 40;
            if (dataEditText.getText().toString().length() > maxDataLength) {
                dataEditText.setText(s.subSequence(0, maxDataLength));
                dataEditText.setSelection(maxDataLength);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean isNameEditTextFilled = !nameEditText.getText().toString().isEmpty();
            boolean isUrlEditTextFilled = !urlEditText.getText().toString().isEmpty();
            boolean isSpinnerAdapterFilled = false;
            if (spinnerAdapter != null) {
                isSpinnerAdapterFilled = !spinnerAdapter.isEmpty();
            }
            addBookmarkButton.setEnabled(isNameEditTextFilled && isUrlEditTextFilled && isSpinnerAdapterFilled);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.add_bookmark));
        }
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        spinner = view.findViewById(R.id.bookmark_category_spinner);
        spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.addAll(categoriesViewModel.getCategoriesNamesList());
        nameEditText = view.findViewById(R.id.bookmark_name_edit_text);
        nameEditText.addTextChangedListener(textWatcher);
        urlEditText = view.findViewById(R.id.bookmark_url_edit_text);
        urlEditText.addTextChangedListener(textWatcher);
        dataEditText = view.findViewById(R.id.bookmark_data_edit_text);
        dataEditText.addTextChangedListener(textWatcher);
        addBookmarkButton = view.findViewById(R.id.add_bookmark_button);
        addCLickListenerForNewBookmarkAndInsertIfValid();
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> resetEditTextViews());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        categoriesViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories.isEmpty()) {
                spinner.setEnabled(false);
                spinnerAdapter.add(getString(R.string.no_category_defined));
            } else {
                spinnerAdapter.clear();
                spinnerAdapter.addAll(categoriesViewModel.getCategoriesNamesList());
                spinner.setEnabled(true);
            }
        });
    }

    public void clearViewAndOpenHomeFragment() {
        resetEditTextViews();
        Toast.makeText(requireActivity(), R.string.bookmark_added, Toast.LENGTH_SHORT).show();
        navController.navigate(R.id.action_addBookmarkFragment_to_homeFragment);
    }

    private void addCLickListenerForNewBookmarkAndInsertIfValid() {
        addBookmarkButton.setOnClickListener(v -> {
            if (URLUtil.isValidUrl(urlEditText.getText().toString())) {
                new Thread(() -> FirebaseBookmarkHelper.addNewBookmark(
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
                            public void onError(int errorStringId) {
                                Toast.makeText(requireActivity(), errorStringId, Toast.LENGTH_SHORT).show();
                            }
                        })).start();
            } else {
                Toast.makeText(requireActivity(), R.string.invalid_url, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetEditTextViews() {
        nameEditText.setText("");
        urlEditText.setText("");
        dataEditText.setText("");
    }

}