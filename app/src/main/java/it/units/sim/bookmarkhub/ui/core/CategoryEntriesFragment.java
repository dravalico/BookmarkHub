package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.units.sim.bookmarkhub.R;

public class CategoryEntriesFragment extends Fragment {
    private static final String ARG = "category_name";
    private String category;

    public static CategoryEntriesFragment newInstance(String param1) {
        CategoryEntriesFragment fragment = new CategoryEntriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_category_entries, container, false);
        EditText editText = view.findViewById(R.id.prova);
        editText.setText(category);
        return view;
    }
}