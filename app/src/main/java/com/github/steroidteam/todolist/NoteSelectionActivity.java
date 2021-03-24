package com.github.steroidteam.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.steroidteam.todolist.notes.Note;

import java.util.ArrayList;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private static NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        Toolbar toolbar = findViewById(R.id.activity_noteselection_toolbar);
        setSupportActionBar(toolbar);
        setTitle("My notes");

        // Filler
        //ArrayList<Note> notes = new ArrayList<>();


        adapter = new NoteAdapter(DBInstance.volatileDatabase.getNoteList());
        ListView listView = findViewById(R.id.activity_noteselection_notelist);
        listView.setAdapter(adapter);
    }




    private class NoteAdapter extends BaseAdapter {

        private final ArrayList<Note> notes;

        public NoteAdapter(@NonNull ArrayList<Note> notes) { this.notes = notes; }

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
            return position; //No need to specify a particular ID, we just return the position.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Note note = (Note) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_note_item, parent, false);
            }

            TextView noteView = convertView.findViewById(R.id.layout_note_textview);
            noteView.setText(note.getTitle());

            noteView.setOnClickListener((view) -> {
                //Note note1 = (Note) view.getTag();
                Intent noteDisplayActivity = new Intent(NoteSelectionActivity.this, NoteDisplayActivity.class);
                noteDisplayActivity.putExtra(EXTRA_NOTE_ID, note.getId().toString());
                startActivity(noteDisplayActivity);
            });

            return convertView;
        }

    }

    public void openNotes(View view) {
        Intent listSelectionActivity = new Intent(NoteSelectionActivity.this, ListSelectionActivity.class);
        startActivity(listSelectionActivity);
    }
}