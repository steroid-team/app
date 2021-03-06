package com.github.steroidteam.todolist.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import androidx.fragment.app.DialogFragment;
import com.github.steroidteam.todolist.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SimpleDialogFragment extends DialogFragment {

    private static final String TITLE_KEY = "title";
    private DialogListener listener;

    public SimpleDialogFragment() {
        super();
    }

    public SimpleDialogFragment newInstance(DialogListener listener, int title) {
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
        this.setArguments(args);

        this.listener = listener;

        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE_KEY);

        Dialog dialog =
                new MaterialAlertDialogBuilder(getActivity())
                        .setTitle(title)
                        .setPositiveButton(
                                R.string.confirm,
                                (dialog1, whichButton) -> listener.onPositiveClick())
                        .setNegativeButton(
                                R.string.cancel,
                                (dialog12, whichButton) -> listener.onNegativeClick())
                        .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }
}
