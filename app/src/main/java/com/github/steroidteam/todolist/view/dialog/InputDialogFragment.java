package com.github.steroidteam.todolist.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import com.github.steroidteam.todolist.R;

public class InputDialogFragment extends DialogFragment {

    private static final String TITLE_KEY = "title";
    private DialogListener listener;

    public InputDialogFragment() {
        super();
    }

    public InputDialogFragment newInstance(DialogListener listener, int title) {
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
        this.setArguments(args);

        this.listener = listener;

        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE_KEY);

        LayoutInflater inflater = this.getLayoutInflater();
        View user_input = inflater.inflate(R.layout.alert_dialog_input, null);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Dialog))
                        .setTitle(title)
                        .setPositiveButton(
                                R.string.confirm,
                                (DialogInterface dialog, int which) -> {
                                    EditText titleInput =
                                            user_input.findViewById(R.id.alert_dialog_edit_text);
                                    String input = titleInput.getText().toString();
                                    listener.onPositiveClick(input);
                                    titleInput.getText().clear();
                                    dialog.dismiss();
                                })
                        .setNegativeButton(
                                R.string.cancel,
                                (dialog12, whichButton) -> listener.onNegativeClick());

        builder.setView(user_input);

        Dialog dialog = builder.show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
