package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.steroidteam.todolist.R;

public class CreditsFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_credits, container, false);

        TextView textView = root.findViewById(R.id.credits_body);

        textView.setText("Add Credits");

        return root;
    }
}
