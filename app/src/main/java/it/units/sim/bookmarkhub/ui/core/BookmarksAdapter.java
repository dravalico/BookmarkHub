package it.units.sim.bookmarkhub.ui.core;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
        Uri uri = Uri.parse(model.url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        Glide.with(activity)
                .asBitmap()
                .load(scheme + "://" + host + "/favicon.ico")
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false))
                .error(R.drawable.image_not_found)
                .into(holder.favicon);
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
        public final ImageView favicon;

        BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.bookmark_name_text_view);
            data = itemView.findViewById(R.id.bookmark_data_text_view);
            favicon = itemView.findViewById(R.id.favicon_image_view);
        }
    }

}

