package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.dialog.AuthDialogFragment;
import com.github.steroidteam.todolist.viewmodel.UserViewModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextView userName;
    private EditText userNameEditable;
    private TextView userMail;
    private EditText userMailEditable;

    private EditText userPwdEditable;

    @Override
    public View onCreateView(
            @NonNull @NotNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        this.userName = root.findViewById(R.id.profile_name_text);
        this.userNameEditable = root.findViewById(R.id.profile_name_edit_text);

        this.userMail = root.findViewById(R.id.profile_mail_text);
        this.userMailEditable = root.findViewById(R.id.profile_mail_edit_text);

        this.userPwdEditable = root.findViewById(R.id.profile_pwd_edit_text);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUser().observe(getViewLifecycleOwner(), this::setViews);

        userViewModel
                .getErrorOccurred()
                .observe(
                        getViewLifecycleOwner(),
                        bool -> {
                            if (bool) {
                                Toast.makeText(
                                                getContext(),
                                                "Error: unable to connect to Firebase or wrong input!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                userViewModel.errorOccurredDone();
                                setViews(
                                        Objects.requireNonNull(userViewModel.getUser().getValue()));
                            }
                        });

        ConstraintLayout editNameLayout = root.findViewById(R.id.profile_name_edit);
        editNameLayout.setVisibility(View.INVISIBLE);
        ConstraintLayout editMailLayout = root.findViewById(R.id.profile_mail_edit);
        editMailLayout.setVisibility(View.INVISIBLE);
        setButtonNameListener(root);
        setButtonMailListener(root);
        initModifPwdBtn(root);

        return root;
    }

    private void setViews(FirebaseUser user) {
        userName.setText(user.getDisplayName());
        userNameEditable.setText(user.getDisplayName());
        userMail.setText(user.getEmail());
        userMailEditable.setText(user.getEmail());
    }

    private void setButtonNameListener(View root) {
        ConstraintLayout editableNameLayout = root.findViewById(R.id.profile_name_edit);

        // Listener Button Edit
        Button buttonDisplayEditLayout = (Button) root.findViewById(R.id.profile_name_edit_btn);
        buttonDisplayEditLayout.setOnClickListener(v -> displayEditLayout(editableNameLayout));

        // Listener Button Save
        Button buttonSaveName = root.findViewById(R.id.profile_name_edit_save);
        buttonSaveName.setOnClickListener(
                view -> {
                    String newName = userNameEditable.getText().toString();
                    userViewModel.updateUser(
                            new UserProfileChangeRequest.Builder().setDisplayName(newName).build());
                    displayEditLayout(editableNameLayout);
                });
    }

    private void setButtonMailListener(View root) {
        ConstraintLayout editableMailLayout = root.findViewById(R.id.profile_mail_edit);

        // Listener Button Edit
        Button buttonDisplayEditLayout = (Button) root.findViewById(R.id.profile_mail_edit_btn);
        buttonDisplayEditLayout.setOnClickListener(
                v -> {
                    if (editableMailLayout.getVisibility() == View.INVISIBLE) {
                        reAuth(editableMailLayout);
                    }
                });

        // Listener Button Save
        Button buttonSaveMail = root.findViewById(R.id.profile_mail_edit_save);
        buttonSaveMail.setOnClickListener(
                view -> {
                    String newMail = userMailEditable.getText().toString();
                    userViewModel.updateUserMail(newMail);
                    displayEditLayout(editableMailLayout);
                });
    }

    private void displayEditLayout(ConstraintLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.INVISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            setEditView();
        }
    }

    private void setEditView() {
        FirebaseUser user = userViewModel.getUser().getValue();
        if (user != null) {
            userNameEditable.setText(user.getDisplayName());
            userMailEditable.setText(user.getEmail());
        }
    }

    private void initModifPwdBtn(View root) {
        ConstraintLayout editablePwdLayout = root.findViewById(R.id.profile_pwd_edit);
        editablePwdLayout.setVisibility(View.INVISIBLE);

        // Listener Button Edit
        Button buttonDisplayEditLayout = (Button) root.findViewById(R.id.profile_pwd_edit_btn);
        buttonDisplayEditLayout.setOnClickListener(
                v -> {
                    if (editablePwdLayout.getVisibility() == View.INVISIBLE) {
                        reAuth(editablePwdLayout);
                    }
                });

        // Listener Button Save
        Button buttonSavePwd = root.findViewById(R.id.profile_pwd_edit_save);
        buttonSavePwd.setOnClickListener(
                view -> {
                    String newPassword = userPwdEditable.getText().toString();
                    userViewModel.updatePassword(newPassword);
                    displayEditLayout(editablePwdLayout);
                });
    }

    private void reAuth(ConstraintLayout view) {
        AuthDialogFragment.AuthDialogListener simpleDialogListener =
                (email, pwd) -> {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, pwd);

                    UserFactory.get()
                            .reauthenticate(credential)
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            displayEditLayout(view);
                                        } else {
                                            Toast.makeText(
                                                            getContext(),
                                                            "Error: You need to re-Authenticate to update critical information",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                };

        DialogFragment newFragment = new AuthDialogFragment().newInstance(simpleDialogListener);
        newFragment.show(getParentFragmentManager(), "auth_dialog");
    }
}
