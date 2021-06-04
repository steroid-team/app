package com.github.steroidteam.todolist.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ListSelectionDialogFragment extends DialogFragment {

    private static final String TITLE_KEY = "title";
    private static final String ITEMS_KEY = "items";
    private DialogInterface.OnClickListener listener;

    public ListSelectionDialogFragment() {
        super();
    }

    public ListSelectionDialogFragment newInstance(
            DialogInterface.OnClickListener listener, int title, int items) {
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
        args.putInt(ITEMS_KEY, items);
        this.setArguments(args);
        this.listener = listener;

        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE_KEY);
        int items = getArguments().getInt(ITEMS_KEY);

        Dialog dialog =
                new MaterialAlertDialogBuilder(getActivity())
                        .setTitle(title)
                        .setItems(items, listener)
                        .create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }
}
