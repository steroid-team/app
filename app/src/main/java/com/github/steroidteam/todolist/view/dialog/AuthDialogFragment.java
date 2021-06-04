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

public class AuthDialogFragment extends DialogFragment {

    private final String DIALOG_TITLE = "Please, you need to re-authenticate!";
    private AuthDialogListener listener;

    public AuthDialogFragment() {
        super();
    }

    public AuthDialogFragment newInstance(AuthDialogListener listener) {

        this.listener = listener;

        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.auth_dialog, null);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Dialog))
                        .setTitle(DIALOG_TITLE)
                        .setPositiveButton(
                                R.string.confirm,
                                (DialogInterface dialog, int which) -> {
                                    EditText email =
                                            dialogLayout.findViewById(R.id.alert_dialog_email);
                                    String inputEmail = email.getText().toString();
                                    email.getText().clear();

                                    EditText pwd =
                                            dialogLayout.findViewById(R.id.alert_dialog_password);
                                    String inputPwd = pwd.getText().toString();
                                    pwd.getText().clear();

                                    listener.onPositiveClick(inputEmail, inputPwd);
                                    dialog.dismiss();
                                })
                        .setNegativeButton(
                                R.string.cancel, (dialog, whichButton) -> dialog.dismiss());

        builder.setView(dialogLayout);

        Dialog dialog = builder.show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public interface AuthDialogListener {

        /**
         * Method to call when the user, click on the positive button and we want to return some
         * inputs.
         */
        void onPositiveClick(String input1, String input2);
    }
}
