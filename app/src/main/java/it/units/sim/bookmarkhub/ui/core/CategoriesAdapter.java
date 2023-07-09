package it.units.sim.bookmarkhub.ui.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;

public class CategoriesAdapter extends FirestoreRecyclerAdapter<Category, CategoriesAdapter.CategoriesViewHolder> {

    public CategoriesAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Category model) {
        holder.item.setText(model.name);
        holder.cardView.setOnClickListener(v -> {
            // TODO
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
            item = itemView.findViewById(R.id.item);
        }
    }

}
