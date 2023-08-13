package it.units.sim.bookmarkhub.ui.core;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
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
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.bookmarkhub.R;
import it.units.sim.bookmarkhub.model.Bookmark;
import it.units.sim.bookmarkhub.repository.FirebaseBookmarkHelper;
import it.units.sim.bookmarkhub.ui.MainViewModel;

public class ModifyBookmarkDialogFragment extends DialogFragment {
    public static final String TAG = "ModifyBookmarkDialogFragment";
    private static final String ARG = "bookmark";
    private Bookmark bookmark;
    private EditText nameEditText;
    private EditText urlEditText;
    private EditText additionalDataEditText;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private MainViewModel mainViewModel;

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bookmark = getArguments().getSerializable(ARG, Bookmark.class);
            } else {
                bookmark = (Bookmark) getArguments().getSerializable(ARG);
            }
        }
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_modify_bookmark, null);
        nameEditText = view.findViewById(R.id.bookmark_name_edit_text);
        urlEditText = view.findViewById(R.id.bookmark_url_edit_text);
        additionalDataEditText = view.findViewById(R.id.bookmark_data_edit_text);
        spinner = view.findViewById(R.id.bookmark_category_spinner);
        spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        setFieldsForModification();
        return createDialog(view);
    }

    private void setFieldsForModification() {
        nameEditText.setText(bookmark.name);
        urlEditText.setText(bookmark.url);
        additionalDataEditText.setText(bookmark.additionalData);
        List<String> categories = mainViewModel.getCategoriesNamesList();
        spinnerAdapter.addAll(categories);
        int defaultPosition = categories.indexOf(bookmark.category);
        if (defaultPosition != -1) {
            spinner.setSelection(defaultPosition);
        }
    }

    @NonNull
    private AlertDialog createDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.modify_bookmark);
        builder.setView(view).setPositiveButton(R.string.confirm_dialog, null).setNegativeButton(R.string.cancel_dialog, (dialog, id) -> dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> setBehaviourOfPositiveButton(alertDialog));
        return alertDialog;
    }

    private void setBehaviourOfPositiveButton(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if ((nameEditText.getText().toString().equals(bookmark.name))
                    && (urlEditText.getText().toString().equals(bookmark.url))
                    && (additionalDataEditText.getText().toString().equals(bookmark.additionalData))
                    && (spinner.getSelectedItem().toString().equals(bookmark.category))) {
                Toast.makeText(requireContext(), R.string.bookmark_modification_error, Toast.LENGTH_SHORT).show();
            } else { //TODO check if name and data aren't too long and if is a valid URL
                bookmark.name = nameEditText.getText().toString();
                bookmark.url = urlEditText.getText().toString();
                bookmark.additionalData = additionalDataEditText.getText().toString();
                bookmark.category = spinner.getSelectedItem().toString();
                new Thread(() -> FirebaseBookmarkHelper.modifyBookmark(
                        bookmark,
                        new FirebaseBookmarkHelper.BookmarkCallback() {
                            @Override
                            public void onSuccess(List<Bookmark> bookmark) {
                                Toast.makeText(requireContext(), R.string.bookmark_modified, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }

                            @Override
                            public void onError(int errorStringId) {
                                Toast.makeText(requireContext(), errorStringId, Toast.LENGTH_SHORT).show();
                            }
                        })).start();
            }
        });
    }

}
