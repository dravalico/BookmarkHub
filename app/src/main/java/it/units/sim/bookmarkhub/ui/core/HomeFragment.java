package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class HomeFragment extends Fragment {
    private CategoryAdapter categoryAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseCategoriesHelper.getQueryForCategoriesListOfCurrentUser(), Category.class)
                .build();
        categoryAdapter = new CategoryAdapter(options);
        recyclerView.setAdapter(categoryAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }

}