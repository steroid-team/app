package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.FirebaseDatabase;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import java.util.UUID;

public class NoteDisplayActivity extends AppCompatActivity {

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_display);

        database =
                new FirebaseDatabase(
                        new FirebaseFileStorageService(
                                FirebaseStorage.getInstance(),
                                FirebaseAuth.getInstance().getCurrentUser()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        UUID id = UUID.fromString(intent.getStringExtra(NoteSelectionActivity.EXTRA_NOTE_ID));
        EditText editText = findViewById(R.id.activity_notedisplay_edittext);

        // Add a click listener to the "back" button to return to the previous activity.
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(
                (view) -> {
                    TextView noteTitle = findViewById(R.id.note_title);
                    Note updatedNote = new Note(noteTitle.getText().toString());

                    updatedNote.setContent(editText.getText().toString());
                    database.putNote(id, updatedNote).thenAccept(str -> finish());
                });

        database.getNote(id)
                .thenAccept(
                        note -> {
                            TextView noteTitle = findViewById(R.id.note_title);
                            noteTitle.setText(note.getTitle());

                            editText.setText(note.getContent());
                        });
    }

    /**
     * This method is called when the user click on the note_header to switch to MapActivity
     *
     * @param view
     */
    public void goToMapActivity(View view) {
        Intent mapActivity = new Intent(NoteDisplayActivity.this, MapsActivity.class);
        startActivity(mapActivity);
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public void setDatabase(Database database) {
        this.database = database;
    }
}
