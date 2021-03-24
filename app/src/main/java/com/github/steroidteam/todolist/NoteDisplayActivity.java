package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.steroidteam.todolist.notes.Note;

import java.util.UUID;

public class NoteDisplayActivity extends AppCompatActivity {

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_display);

        Toolbar toolbar = findViewById(R.id.activity_notedisplay_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        UUID id = UUID.fromString(intent.getStringExtra(NoteSelectionActivity.EXTRA_NOTE_ID));
        note = DBInstance.volatileDatabase.getNote(id);

        //setTitle("Lorem ipsum");
        EditText view = findViewById(R.id.activity_notedisplay_edittext);
        /*view.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation" +
                " ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in" +
                " voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non" +
                " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");*/
        setTitle(note.getTitle());
        view.setText(note.getContent());

    }

    public void updateNote(View view) {
        EditText editText = findViewById(R.id.activity_notedisplay_edittext);
        note.setContent(editText.getText().toString());
        DBInstance.volatileDatabase.putNote(note);
    }
}