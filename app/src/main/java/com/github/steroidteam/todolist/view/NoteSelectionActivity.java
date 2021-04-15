package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.adapter.NoteAdapter;
import java.util.ArrayList;
import java.util.List;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private RecyclerView recyclerView;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        recyclerView = findViewById(R.id.activity_noteselection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter.NoteCustomListener customListener =
                new NoteAdapter.NoteCustomListener() {
                    @Override
                    public void onClickCustom(NoteAdapter.NoteHolder holder) {
                        openNote(holder);
                    }
                };

        adapter = new NoteAdapter(setupSomeNotes(), customListener);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    // THIS METHOD JUST CREATE A LIST OF NOTE AS I DON'T HAVE THE DATABASE FOR NOTE
    // THIS WILL BE DELETE
    private List<Note> setupSomeNotes() {
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

        return notes;
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
