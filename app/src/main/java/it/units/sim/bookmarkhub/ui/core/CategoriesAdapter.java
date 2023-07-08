package it.units.sim.bookmarkhub.ui.core;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.CategoriesEntity;

public class CategoriesAdapter extends FirestoreRecyclerAdapter<CategoriesEntity, CategoriesAdapter.CategoriesViewHolder> {

    public CategoriesAdapter(@NonNull FirestoreRecyclerOptions<CategoriesEntity> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull CategoriesEntity model) {
        holder.firstname.setText(model.ownerId);
        holder.lastname.setText(model.categories.toString());
        Log.d("", String.valueOf(this.getItemCount()));
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.category, group, false);
        return new CategoriesViewHolder(view);
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        public TextView firstname, lastname, age;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            firstname = itemView.findViewById(R.id.firstname);
            lastname = itemView.findViewById(R.id.lastname);
            age = itemView.findViewById(R.id.age);
        }
    }

}
