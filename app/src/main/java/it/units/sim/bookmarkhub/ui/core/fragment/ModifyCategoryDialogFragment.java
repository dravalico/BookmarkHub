package it.units.sim.bookmarkhub.ui.core.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
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
import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCallback;
import it.units.sim.bookmarkhub.repository.FirebaseCategoryHelper;

public class ModifyCategoryDialogFragment extends DialogFragment {
    public static final String TAG = "ModifyCategoryDialogFragment";
    private static final String ARG = "category";
    private Category category;
    private EditText nameEditText;

    public static ModifyCategoryDialogFragment newInstance(Category category) {
        ModifyCategoryDialogFragment fragment = new ModifyCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                category = getArguments().getSerializable(ARG, Category.class);
            } else {
                category = (Category) getArguments().getSerializable(ARG);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_modify_category, null);
        nameEditText = view.findViewById(R.id.category_name_edit_text);
        nameEditText.setText(category.name);
        return createDialog(view);
    }

    @NonNull
    private AlertDialog createDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.modify_category)
                .setView(view)
                .setPositiveButton(R.string.confirm_dialog, null)
                .setNegativeButton(R.string.cancel_dialog, (dialog, id) -> dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> setBehaviourOfPositiveButton(alertDialog));
        return alertDialog;
    }

    private void setBehaviourOfPositiveButton(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (nameEditText.getText().toString().equals(category.name)) {
                Toast.makeText(requireContext(), R.string.category_modification_error, Toast.LENGTH_SHORT).show();
            } else if (nameEditText.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), R.string.category_name_not_empty, Toast.LENGTH_SHORT).show();
            } else {
                Category categoryNew = new Category();
                categoryNew.id = category.id;
                categoryNew.userId = category.userId;
                categoryNew.name = nameEditText.getText().toString();
                FirebaseCategoryHelper.modifyCategoryName(
                        category,
                        categoryNew,
                        new FirebaseCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireContext(), R.string.category_modified, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }

                            @Override
                            public void onFailure(int errorStringId) {
                                Toast.makeText(requireContext(), errorStringId, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

}
