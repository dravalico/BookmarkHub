package it.units.sim.bookmarkhub.ui.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;

public class CategoriesAdapter extends FirestoreRecyclerAdapter<Category, CategoriesAdapter.CategoriesViewHolder> {
    private final FragmentManager fragmentManager;

    public CategoriesAdapter(@NonNull FirestoreRecyclerOptions<Category> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Category model) {
        holder.item.setText(model.name);
        holder.cardView.setOnClickListener(v -> {
            CategoryEntriesFragment fragment = new CategoryEntriesFragment();
            Bundle args = new Bundle();
            args.putString("category_name", model.name);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.category, group, false);
        return new CategoriesViewHolder(view);
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView item;

        CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            item = itemView.findViewById(R.id.category_item_text_view);
        }
    }

}
