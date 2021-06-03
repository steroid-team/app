package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;

    @Override
    public View onCreateView(
            @NonNull @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        this. user = UserFactory.get();

        setNameField(root);
        setMailField(root);

        return root;
    }

    private void setNameField(View root) {
        TextView view_name = root.findViewById(R.id.profile_name_text);
        view_name.setText(user.getDisplayName());

        LinearLayout editNameLayout = root.findViewById(R.id.profile_name_edit);
        editNameLayout.setVisibility(View.INVISIBLE);

        Button button = root.findViewById(R.id.profile_name_edit_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_name.setText("OIUIIIIIIIIII");
            }
        });
    }

    private void setMailField(View root) {
        TextView view_name = root.findViewById(R.id.profile_mail_text);
        view_name.setText(user.getEmail());
    }
}
