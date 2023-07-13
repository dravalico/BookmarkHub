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
import it.units.sim.bookmarkhub.model.Bookmark;

public class BookmarksAdapter extends FirestoreRecyclerAdapter<Bookmark, BookmarksAdapter.BookmarkViewHolder> {

    public BookmarksAdapter(@NonNull FirestoreRecyclerOptions<Bookmark> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position, @NonNull Bookmark model) {
        holder.name.setText(model.name);
        holder.url.setText(model.url);
        holder.cardView.setOnClickListener(v -> {
            // TODO
        });
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.bookmark, group, false);
        return new BookmarkViewHolder(view);
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView name;
        public final TextView url;

        BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
        }
    }

}

