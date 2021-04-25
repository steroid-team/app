package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import java.util.ArrayList;
import java.util.UUID;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private static NoteAdapter adapter;
    private Database database = null;
    ArrayList<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        adapter = new NoteAdapter(notes);
        ListView listView = findViewById(R.id.activity_noteselection_notelist);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

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

    private class NoteAdapter extends BaseAdapter {

        private final ArrayList<Note> notes;

        public NoteAdapter(@NonNull ArrayList<Note> notes) {
            this.notes = notes;
        }

        @Override
        public int getCount() {
            return notes.size();
        }

        @Override
        public Object getItem(int position) {
            return notes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position; // No need to specify a particular ID, we just return the position.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Note note = (Note) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView =
                        LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.layout_note_item, parent, false);
            }

            TextView noteViewTitle = convertView.findViewById(R.id.layout_note_title);
            noteViewTitle.setText(note.getTitle());

            ConstraintLayout noteView = convertView.findViewById(R.id.layout_note);
            noteView.setOnClickListener(
                    (view) -> {
                        // Note note1 = (Note) view.getTag();
                        Intent noteDisplayActivity =
                                new Intent(NoteSelectionActivity.this, NoteDisplayActivity.class);
                        noteDisplayActivity.putExtra(EXTRA_NOTE_ID, note.getId().toString());
                        startActivity(noteDisplayActivity);
                    });

            return convertView;
        }
    }

    public void openNotes(View view) {
        Intent listSelectionActivity =
                new Intent(NoteSelectionActivity.this, ListSelectionActivity.class);
        startActivity(listSelectionActivity);
    }
}
