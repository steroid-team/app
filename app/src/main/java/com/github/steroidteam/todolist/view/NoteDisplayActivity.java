package com.github.steroidteam.todolist.view;

import static com.github.steroidteam.todolist.view.MapsActivity.KEY_LOCATION;
import static com.github.steroidteam.todolist.view.MapsActivity.KEY_NAME_LOCATION;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.google.android.gms.maps.model.LatLng;
import java.io.InputStream;
import java.util.UUID;

public class NoteDisplayActivity extends AppCompatActivity {
    private int LAUNCH_SECOND_ACTIVITY = 2;

    public static final int PICK_IMAGE = 1;

    private Database database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_display);

        database = DatabaseFactory.getDb();

        Intent intent = getIntent();
        UUID id = UUID.fromString(intent.getStringExtra(NoteSelectionActivity.EXTRA_NOTE_ID));
        EditText editText = findViewById(R.id.activity_notedisplay_edittext);

        // Add a click listener to the "back" button to return to the previous activity.
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(
                (view) -> {
                    finish();
                });

        database.getNote(id)
                .thenAccept(
                        note -> {
                            TextView noteTitle = findViewById(R.id.note_title);
                            noteTitle.setText(note.getTitle());

                            editText.setText(note.getContent());
                        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = getIntent();
        UUID id = UUID.fromString(intent.getStringExtra(NoteSelectionActivity.EXTRA_NOTE_ID));

        EditText editText = findViewById(R.id.activity_notedisplay_edittext);
        TextView noteTitle = findViewById(R.id.note_title);

        database.getNote(id)
                .thenCompose(
                        note -> {
                            note.setTitle(noteTitle.getText().toString());
                            note.setContent(editText.getText().toString());

                            return database.putNote(id, note);
                        });
    }

    public void pickFile(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            String location = data.getStringExtra(KEY_NAME_LOCATION);
            LatLng latLng = data.getParcelableExtra(KEY_LOCATION);
            setLocationNote(latLng, location);
        }
        if (requestCode == PICK_IMAGE && data != null) {
            Uri uri = data.getData();
            ConstraintLayout header = findViewById(R.id.note_header);
            Bitmap bitmap = null;
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                Toast.makeText(
                                getApplicationContext(),
                                "Error: could not display the image",
                                Toast.LENGTH_LONG)
                        .show();
            }
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            header.setBackgroundTintList(null);
            header.setBackground(ob);
        }
    }

    /**
     * This method is called when the user click on the note_header to switch to MapActivity
     *
     * @param view
     */
    public void goToMapActivity(View view) {
        Intent mapActivity = new Intent(NoteDisplayActivity.this, MapsActivity.class);
        startActivityForResult(mapActivity, LAUNCH_SECOND_ACTIVITY);
    }

    public void setLocationNote(LatLng latLng, String location) {
        // TODO : Change the location of the note when the activity will be link with real note
        setContentView(R.layout.activity_note_display);

        if (latLng != null && location != null) {
            TextView locationText = (TextView) findViewById(R.id.note_location);
            locationText.setText(location);
        }
    }
}
