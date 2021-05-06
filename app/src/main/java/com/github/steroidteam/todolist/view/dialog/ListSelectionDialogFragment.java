package com.github.steroidteam.todolist.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.github.steroidteam.todolist.R;

public class ListSelectionDialogFragment extends DialogFragment {

    private static final String TITLE_KEY = "title";
    private static final String ITEMS_KEY = "items";
    // private List<ItemListener> listeners;
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

        // this.listeners = listeners;
        // this.listener = (dialog, position) -> listeners.get(position);
        this.listener = listener;

        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE_KEY);
        int items = getArguments().getInt(ITEMS_KEY);

        Dialog dialog =
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Dialog))
                        .setTitle(title)
                        .setItems(items, listener)
                        .create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
