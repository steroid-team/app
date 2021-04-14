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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.adapter.NoteAdapter;

import java.util.ArrayList;

public class NoteSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "id";

    private RecyclerView recyclerView;
    private NoteAdapter adapter;

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

        recyclerView = findViewById(R.id.activity_noteselection_notelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter.NoteCustomListener customListener =
                new NoteAdapter.NoteCustomListener() {
                    @Override
                    public void onClickCustom(NoteAdapter.NoteHolder holder) {
                        openNotes(holder);
                    }
                };

        adapter = new NoteAdapter(notes, customListener);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void openNotes(NoteAdapter.NoteHolder holder) {
        Intent itemViewActivity = new Intent(NoteSelectionActivity.this, NoteDisplayActivity.class);
        itemViewActivity.putExtra(EXTRA_NOTE_ID, adapter.getIdOfNote(holder.getAdapterPosition()).toString());
        startActivity(itemViewActivity);
    }
}
