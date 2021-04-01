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
import com.github.steroidteam.todolist.model.notes.Note;
import java.util.ArrayList;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private static NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        // Filler
        ArrayList<Note> notes = new ArrayList<>();
        Note note1 = new Note("Lorem ipsum");
        note1.setContent(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"
                        + " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                        + " ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in"
                        + " voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non"
                        + " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        Note note2 = new Note("Note 2");
        note2.setContent("This is the second note");
        Note note3 = new Note("Note 3");
        note3.setContent("This is the third note");

        notes.add(note1);
        notes.add(note2);
        notes.add(note3);

        adapter = new NoteAdapter(notes);
        ListView listView = findViewById(R.id.activity_noteselection_notelist);
        listView.setAdapter(adapter);
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
