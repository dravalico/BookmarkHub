package it.units.sim.bookmarkhub.ui.core.fragment;

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

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.repository.FirebaseCallback;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class AddCategoryDialogFragment extends DialogFragment {
    public static final String TAG = "AddCategoryDialogFragment";
    private EditText nameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.add_category));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_modify_category, null);
        nameEditText = view.findViewById(R.id.category_name_edit_text);
        builder.setView(view)
                .setPositiveButton(R.string.add_category_dialog, null)
                .setNegativeButton(R.string.cancel_dialog, (dialog, id) -> dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> setBehaviourOfPositiveButton(alertDialog));
        return alertDialog;
    }

    private void setBehaviourOfPositiveButton(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (nameEditText.getText().toString().equals("")) {
                Toast.makeText(requireActivity(), R.string.category_name_not_empty, Toast.LENGTH_SHORT).show();
            } else {
                FirebaseCategoryHelper.addNewCategoryIfNotAlreadySaved(
                        nameEditText.getText().toString(),
                        new FirebaseCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireActivity(), R.string.category_added, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }

                            @Override
                            public void onFailure(int errorStringId) {
                                Toast.makeText(requireActivity(), errorStringId, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

}
