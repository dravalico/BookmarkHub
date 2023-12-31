package it.units.sim.bookmarkhub.ui.core.adapter;

import android.annotation.SuppressLint;
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

import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {
    private List<Bookmark> bookmarks;
    private final Activity activity;

    public BookmarksAdapter(List<Bookmark> bookmarks, Activity activity) {
        this.bookmarks = bookmarks;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BookmarksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark, parent, false);
        return new BookmarksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarksAdapter.ViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.nameTextView.setText(bookmark.name);
        holder.additionalDataTextView.setText(bookmark.additionalData);
        Uri uri = Uri.parse(bookmark.url);
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
                .into(holder.faviconImageView);
        holder.cardView.setOnClickListener(v ->
                new CustomTabsIntent.Builder().build().launchUrl(activity, Uri.parse(bookmark.url))
        );
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBookmarksList(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        notifyDataSetChanged();
    }

    public Bookmark getItem(int i) {
        return bookmarks.get(i);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView nameTextView;
        public final TextView additionalDataTextView;
        public final ImageView faviconImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            nameTextView = itemView.findViewById(R.id.bookmark_name_text_view);
            additionalDataTextView = itemView.findViewById(R.id.bookmark_data_text_view);
            faviconImageView = itemView.findViewById(R.id.favicon_image_view);
        }
    }

}
