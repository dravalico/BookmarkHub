package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.CategoriesEntity;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class HomeFragment extends Fragment {
    private CategoriesAdapter categoriesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<CategoriesEntity> options = new FirestoreRecyclerOptions.Builder<CategoriesEntity>()
                .setQuery(FirebaseCategoriesHelper.getQueryForCategoriesListOfCurrentUser(), CategoriesEntity.class)
                .build();
        categoriesAdapter = new CategoriesAdapter(options);
        recyclerView.setAdapter(categoriesAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        categoriesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoriesAdapter.stopListening();
    }

}