package it.units.sim.bookmarkhub.ui.core;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;

public class BookmarksAdapter extends FirestoreRecyclerAdapter<Bookmark, BookmarksAdapter.BookmarkViewHolder> {
    private final Activity activity;

    public BookmarksAdapter(@NonNull FirestoreRecyclerOptions<Bookmark> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position, @NonNull Bookmark model) {
        holder.name.setText(model.name);
        holder.data.setText(model.data);
        holder.cardView.setOnClickListener(v ->
                new CustomTabsIntent.Builder().build().launchUrl(activity, Uri.parse(model.url))
        );
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
        public final TextView data;

        BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            data = itemView.findViewById(R.id.data);
        }
    }

}

