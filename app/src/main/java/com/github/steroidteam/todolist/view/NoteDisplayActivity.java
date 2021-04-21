package com.github.steroidteam.todolist.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.steroidteam.todolist.R;
import com.google.android.gms.maps.model.LatLng;
import java.util.UUID;

public class NoteDisplayActivity extends AppCompatActivity {
    private int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_display);

        // Add a click listener to the "back" button to return to the previous activity.
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> finish());

        Intent intent = getIntent();
        UUID id = UUID.fromString(intent.getStringExtra(NoteSelectionActivity.EXTRA_NOTE_ID));

        TextView noteTitle = findViewById(R.id.note_title);
        noteTitle.setText("Lorem ipsum");

        EditText view = findViewById(R.id.activity_notedisplay_edittext);
        view.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"
                        + " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                        + " ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in"
                        + " voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non"
                        + " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            String location = data.getStringExtra("nameLocation");
            LatLng latLng = data.getParcelableExtra("location");
            setLocationNote(latLng, location);
        }
    }
}
