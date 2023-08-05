package it.units.sim.bookmarkhub.ui.core;

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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class CategoriesAdapter extends FirestoreRecyclerAdapter<Category, CategoriesAdapter.CategoriesViewHolder> {
    private final FragmentManager fragmentManager;

    public CategoriesAdapter(@NonNull FirestoreRecyclerOptions<Category> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Category model) {
        holder.itemTextView.setText(model.name);
        holder.itemTextView.setClickable(false);
        holder.cardView.setOnClickListener(view -> {
            CategoryEntriesFragment fragment = new CategoryEntriesFragment();
            Bundle args = new Bundle();
            args.putString("category_name", model.name);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, fragment).addToBackStack(null).commit();
        });
        holder.menuImageButton.setOnClickListener(view -> showPopupMenu(view, model));
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

    private static void showConfirmationDialogForDeletion(View view, Category category1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(category1.name);
        builder.setMessage(R.string.confirm_category_deletion);
        builder.setPositiveButton(R.string.confirm_dialog, (dialogInterface, i) ->
                new Thread(() -> FirebaseCategoryHelper.deleteCategoryAndContent(
                        category1,
                        new FirebaseCategoryHelper.CategoriesCallback() {
                            @Override
                            public void onSuccess(List<Category> category) {
                                String msg = category1.name + " " + view.getResources().getString(R.string.deleted);
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

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.category, group, false);
        return new CategoriesViewHolder(view);
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView itemTextView;
        public final ImageButton menuImageButton;

        CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            itemTextView = itemView.findViewById(R.id.category_item_text_view);
            menuImageButton = itemView.findViewById(R.id.menu_image_button);
        }
    }

}
