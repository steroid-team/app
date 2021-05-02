package com.github.steroidteam.todolist.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.adapter.NoteArrayListAdapter;
import com.github.steroidteam.todolist.viewmodel.NoteSelectionViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class NoteSelectionFragment extends Fragment {

    public static final String NOTE_ID_KEY = "id";
    ArrayList<Note> notes;
    private NoteSelectionViewModel viewModel;
    private NoteArrayListAdapter adapter;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_selection, container, false);

        root.findViewById(R.id.create_note_button).setOnClickListener(this::createNote);

        RecyclerView recyclerView = root.findViewById(R.id.activity_noteselection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteArrayListAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        viewModel = new NoteSelectionViewModel();
        viewModel
                .getNoteList()
                .observe(
                        getActivity(),
                        (noteList) -> {
                            adapter.setNoteList(noteList);
                        });

        return root;
    }

    private NoteArrayListAdapter.NoteHolder.NoteCustomListener createCustomListener() {
        return new NoteArrayListAdapter.NoteHolder.NoteCustomListener() {
            @Override
            public void onClickCustom(NoteArrayListAdapter.NoteHolder holder) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(NOTE_ID_KEY, holder.getId());
                Navigation.findNavController(holder.itemView).navigate(R.id.nav_note_display, bundle);
            }
        }
    }

    public void createNote(View view) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.Dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.add_todo_suggestion));

        LayoutInflater inflater = this.getLayoutInflater();
        View user_input = inflater.inflate(R.layout.alert_dialog_input, null);

        builder.setView(user_input);

        builder.setPositiveButton(
                getString(R.string.add_note),
                (DialogInterface dialog, int which) -> {
                    EditText titleInput = user_input.findViewById(R.id.alert_dialog_edit_text);
                    String title = titleInput.getText().toString();
                    if (title.length() > 0) viewModel.putNote(title);
                    titleInput.getText().clear();
                    dialog.dismiss();
                });
        builder.setNegativeButton(
                getString(R.string.cancel),
                (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                });
        Dialog dialog = builder.show();
        dialog.getWindow().setGravity(0x00000035);
        dialog.setCanceledOnTouchOutside(false);
    }
}
