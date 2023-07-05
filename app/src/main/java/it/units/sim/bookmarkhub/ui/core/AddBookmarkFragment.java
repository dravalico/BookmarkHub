package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.persistence.FirebaseManager;
import it.units.sim.bookmarkhub.persistence.DatabaseDataListener;

public class AddBookmarkFragment extends Fragment implements DatabaseDataListener<List<String>> {
    private ArrayAdapter<String> spinnerAdapter;

    public AddBookmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        FirebaseManager.retrieveCategoriesListOfCurrentUser(this);
        Spinner spinner = view.findViewById(R.id.bookmarkCategorySpinner);
        spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onSuccess(List<String> data) {
        spinnerAdapter.addAll(data);
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}