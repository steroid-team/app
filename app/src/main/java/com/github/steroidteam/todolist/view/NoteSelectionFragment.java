package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.database.LocalCachedDatabase;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.adapter.NoteAdapter;
import java.util.ArrayList;
import java.util.UUID;

public class NoteSelectionFragment extends Fragment {

    public static final String NOTE_ID_KEY = "id";
    ArrayList<Note> notes;
    private Database database = null;
    private NoteAdapter adapter;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_selection, container, false);

        root.findViewById(R.id.create_note_button).setOnClickListener(this::createNote);

        RecyclerView recyclerView = root.findViewById(R.id.activity_noteselection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        notes = new ArrayList<>();
        NoteAdapter.NoteCustomListener customListener = this::openNote;

        adapter = new NoteAdapter(notes, customListener);
        recyclerView.setAdapter(adapter);

        database = DatabaseFactory.getDb();
        database.getNotesList()
                .thenAccept(
                        uuids -> {
                            for (UUID uuid : uuids) {
                                database.getNote(uuid)
                                        .thenAccept(
                                                note -> {
                                                    notes.add(note);
                                                    adapter.notifyDataSetChanged();
                                                });
                            }
                        });

        return root;
    }

    public void createNote(View view) {
        Note newNote = new Note("New note");

        database.putNote(newNote.getId(), newNote)
                .thenAccept(
                        str -> {
                            notes.add(newNote);
                            adapter.notifyDataSetChanged();
                        });
    }

    public void openNote(NoteAdapter.NoteHolder holder) {
        Bundle bundle = new Bundle();
        bundle.putString(NOTE_ID_KEY, adapter.getIdOfNote(holder.getAdapterPosition()).toString());
        Navigation.findNavController(getView()).navigate(R.id.nav_note_display, bundle);
    }
}
