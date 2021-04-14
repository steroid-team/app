package com.github.steroidteam.todolist.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.github.steroidteam.todolist.R;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

public class NoteDisplayActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

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

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("perm","Permission READ_EXTERNAL_STORAGE is granted");
        } else {

            Log.v("perm","Permission READ_EXTERNAL_STORAGE is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("perm","Permission WRITE_EXTERNAL_STORAGE is granted");
        } else {

            Log.v("perm","Permission WRITE_EXTERNAL_STORAGE is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

    }

    public void pickFile(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri uri = data.getData();
            ConstraintLayout header = findViewById(R.id.note_header);
            Bitmap bitmap = null;
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) { }
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            header.setBackgroundTintList(null);
            header.setBackground(ob);
        }
    }
}
