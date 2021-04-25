package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.adapter.NoteAdapter;
import java.util.ArrayList;
import java.util.UUID;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private Database database = null;
    ArrayList<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        recyclerView = findViewById(R.id.activity_noteselection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

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
        Intent itemViewActivity = new Intent(NoteSelectionActivity.this, NoteDisplayActivity.class);
        itemViewActivity.putExtra(
                EXTRA_NOTE_ID, adapter.getIdOfNote(holder.getAdapterPosition()).toString());
        startActivity(itemViewActivity);
    }

    public void goToTODOSelection(View view) {
        Intent listSelectionActivity =
                new Intent(NoteSelectionActivity.this, ListSelectionActivity.class);
        startActivity(listSelectionActivity);
    }
}
