package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class HomeFragment extends Fragment implements MenuProvider {
    private CategoriesAdapter categoriesAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        requireActivity().addMenuProvider(this);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Your categories");
        }
        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseCategoriesHelper.getQueryForCategoriesListOfCurrentUser(null,
                        Query.Direction.ASCENDING), Category.class)
                .build();
        categoriesAdapter = new CategoriesAdapter(options, getParentFragmentManager());
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
        requireActivity().removeMenuProvider(this);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.order_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.name_order_a_z) {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStackImmediate();
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_nav_host_fragment, HomeFragment.class, null)
                        .commit();
            }
            return true;
        }
        return false;
    }

}