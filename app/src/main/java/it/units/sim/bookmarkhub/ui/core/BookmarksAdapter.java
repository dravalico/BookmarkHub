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
import it.units.sim.bookmarkhub.model.Bookmark;

public class BookmarksAdapter extends FirestoreRecyclerAdapter<Bookmark, BookmarksAdapter.BookmarkViewHolder> {
    private final FragmentManager fragmentManager;

    public BookmarksAdapter(@NonNull FirestoreRecyclerOptions<Bookmark> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position, @NonNull Bookmark model) {
        holder.name.setText(model.name);
        holder.url.setText(model.url);
        holder.cardView.setOnClickListener(v -> {
            BookmarkWebViewFragment fragment = new BookmarkWebViewFragment();
            Bundle args = new Bundle();
            args.putString("url", model.url);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
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

