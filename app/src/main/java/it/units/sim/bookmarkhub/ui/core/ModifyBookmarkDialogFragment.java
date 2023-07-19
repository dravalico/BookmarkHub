package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.ui.util.ViewUtil;

public class ModifyBookmarkDialogFragment extends DialogFragment {
    public static final String TAG = "ModifyBookmarkDialogFragment";
    private static final String ARG = "bookmark";
    private Bookmark bookmark;

    public static ModifyBookmarkDialogFragment newInstance(Bookmark bookmark) {
        ModifyBookmarkDialogFragment fragment = new ModifyBookmarkDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG, bookmark);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookmark = (Bookmark) getArguments().getSerializable(ARG); // TODO find an alternative
        }
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Modify '" + bookmark.name + "' bookmark"); // TODO improve it to allow extraction
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_modify_bookmark, null);
        EditText nameEditText = view.findViewById(R.id.bookmark_name_edit_text);
        nameEditText.setText(bookmark.name);
        EditText urlEditText = view.findViewById(R.id.bookmark_url_edit_text);
        urlEditText.setText(bookmark.url);
        EditText dataEditText = view.findViewById(R.id.bookmark_data_edit_text);
        dataEditText.setText(bookmark.data);
        Spinner spinner = view.findViewById(R.id.bookmark_category_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        ViewUtil.fetchCategoriesFromFirebase(requireContext(), spinnerAdapter);
        builder.setView(view)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if ((nameEditText.getText().toString().equals(bookmark.name)) &&
                        (urlEditText.getText().toString().equals(bookmark.url)) &&
                        (dataEditText.getText().toString().equals(bookmark.data))) {
                    Toast.makeText(requireContext(), "You have to modify at least one field", Toast.LENGTH_SHORT).show();
                } else { // TODO check if name and data aren't too long and if is a valid URL
                    bookmark.name = nameEditText.getText().toString();
                    bookmark.url = urlEditText.getText().toString();
                    bookmark.data = dataEditText.getText().toString();
                    new Thread(() ->
                            FirebaseBookmarkHelper.modifyBookmark(bookmark, new FirebaseBookmarkHelper.BookmarkCallback() {
                                @Override
                                public void onSuccess(List<Bookmark> bookmark) {
                                    Toast.makeText(requireContext(), "Modification completed successfully",
                                            Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            })).start();
                }
            });
        });
        return alertDialog;
    }

}
