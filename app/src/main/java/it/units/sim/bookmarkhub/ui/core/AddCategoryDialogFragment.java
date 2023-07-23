package it.units.sim.bookmarkhub.ui.core;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class AddCategoryDialogFragment extends DialogFragment {
    public static final String TAG = "AddCategoryDialogFragment";
    private EditText nameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Add category");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_category_name, null);
        nameEditText = view.findViewById(R.id.category_name_edit_text);
        builder.setView(view)
                .setPositiveButton("Add category", null)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> setBehaviourOfPositiveButton(alertDialog));
        return alertDialog;
    }

    private void setBehaviourOfPositiveButton(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (nameEditText.getText().toString().equals("")) {
                Toast.makeText(requireActivity(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() ->
                        FirebaseCategoriesHelper.addNewCategoryIfNotAlreadySaved(
                                nameEditText.getText().toString(),
                                new FirebaseCategoriesHelper.CategoriesCallback() {
                                    @Override
                                    public void onSuccess(List<Category> category) {
                                        Toast.makeText(requireActivity(), "Category added", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        )
                ).start();
            }
        });
    }

}
