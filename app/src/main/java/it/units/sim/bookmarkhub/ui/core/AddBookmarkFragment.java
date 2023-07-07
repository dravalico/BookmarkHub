package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.CategoriesEntity;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class AddBookmarkFragment extends Fragment {
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        Spinner spinner = view.findViewById(R.id.bookmarkCategorySpinner);
        spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        FirebaseCategoriesHelper.getCategoriesListOfCurrentUser(new FirebaseCategoriesHelper.CategoriesCallback() {
            @Override
            public void onSuccess(CategoriesEntity categoriesEntity) {
                AddBookmarkFragment.this.setAdapterSpinnerValues(categoriesEntity.categories);
            }

            @Override
            public void onError(Exception error) {
                AddBookmarkFragment.this.showToast(error.getMessage());
            }
        });
        view.findViewById(R.id.addBookmarkButton).setOnClickListener(v -> {
            EditText nameEditText = view.findViewById(R.id.bookmarkNameEditText);
            EditText urlEditText = view.findViewById(R.id.bookmarkUrlEditText);
            FirebaseBookmarkHelper.addNewBookmark(nameEditText.getText().toString(), urlEditText.getText().toString(),
                    spinner.getSelectedItem().toString());
        });
        return view;
    }

    public void setAdapterSpinnerValues(List<String> values) {
        spinnerAdapter.addAll(values);
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}