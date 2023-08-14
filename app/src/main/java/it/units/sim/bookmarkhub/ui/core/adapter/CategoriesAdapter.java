package it.units.sim.bookmarkhub.ui.core.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;
import it.units.sim.bookmarkhub.ui.core.fragment.BookmarksFragment;
import it.units.sim.bookmarkhub.ui.core.fragment.ModifyBookmarkDialogFragment;
import it.units.sim.bookmarkhub.ui.core.fragment.ModifyCategoryDialogFragment;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categories;
    private final FragmentManager fragmentManager;

    public CategoriesAdapter(List<Category> categories, FragmentManager fragmentManager) {
        this.categories = categories;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.itemTextView.setText(category.name);
        holder.itemTextView.setClickable(false);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        String dateAsString = dateFormat.format(category.creationDate);
        holder.dateTextView.setText(dateAsString);
        holder.cardView.setOnClickListener(view -> {
            BookmarksFragment fragment = new BookmarksFragment();
            Bundle args = new Bundle();
            args.putString("category_name", category.name);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, fragment).addToBackStack(null).commit();
        });
        holder.menuImageButton.setOnClickListener(view -> showPopupMenu(view, category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCategoriesList(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    private void showPopupMenu(View view, Category category) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.card_view_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                ModifyCategoryDialogFragment dialogFragment =
                        ModifyCategoryDialogFragment.newInstance(category);
                dialogFragment.show(fragmentManager, ModifyBookmarkDialogFragment.TAG);
                return true;
            }
            if (itemId == R.id.action_delete) {
                showConfirmationDialogForDeletion(view, category);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private static void showConfirmationDialogForDeletion(View view, Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(category.name);
        builder.setMessage(R.string.confirm_category_deletion);
        builder.setPositiveButton(R.string.confirm_dialog, (dialogInterface, i) ->
                new Thread(() -> FirebaseCategoryHelper.deleteCategoryAndContent(
                        category,
                        new FirebaseCategoryHelper.CategoriesCallback() {
                            @Override
                            public void onSuccess(List<Category> categories) {
                                String msg = category.name + " " + view.getResources().getString(R.string.deleted);
                                Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int errorStringId) {
                                Toast.makeText(view.getContext(), errorStringId, Toast.LENGTH_SHORT).show();
                            }
                        })).start());
        builder.setNegativeButton(R.string.cancel_dialog, null);
        builder.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView itemTextView;
        public final TextView dateTextView;
        public final ImageButton menuImageButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            itemTextView = itemView.findViewById(R.id.category_item_text_view);
            dateTextView = itemView.findViewById(R.id.category_date_text_view);
            menuImageButton = itemView.findViewById(R.id.menu_image_button);
        }
    }

}