package com.github.steroidteam.todolist.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import jp.wasabeef.richeditor.RichEditor;

public class NoteDisplayFragment extends Fragment {
    public static LatLng position;
    public static String locationName;
    public static String lastDrawingUri;
    private Database database;
    private UUID noteID;
    private RichEditor richEditor;
    private ActivityResultLauncher<String> headerImagePickerActivityLauncher;
    private ActivityResultLauncher<String> embeddedImagePickerActivityLauncher;
    private final String IMAGE_MIME_TYPE = "image/*";
    int imageDisplayWidth;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_display, container, false);

        setOnClickListeners(root);

        // Rich text editor setup.
        richEditor = root.findViewById(R.id.notedisplay_text_editor);
        richEditor.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_grey));
        int padding = (int) getResources().getDimension(R.dimen.note_body_padding);
        richEditor.setPadding(padding, padding, padding, 0);

        // The width for any embedded image should be the editor's width. Do the math to
        // transform the dp to px, and subtract the lateral padding.
        imageDisplayWidth =
                (int)
                                Math.floor(
                                        getResources().getDisplayMetrics().widthPixels
                                                / getResources().getDisplayMetrics().density)
                        - 2 * padding;

        // Get the UUID of the currently selected note.
        noteID = (UUID) getArguments().getSerializable(NoteSelectionFragment.NOTE_ID_KEY);

        database = DatabaseFactory.getDb();
        database.getNote(noteID)
                .thenAccept(
                        note -> {
                            TextView noteTitle = root.findViewById(R.id.note_title);
                            noteTitle.setText(note.getTitle());
                            richEditor.setHtml(note.getContent());
                        });

        headerImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(), this::updateHeaderImage);
        embeddedImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            richEditor.insertImage(uri.toString(), "", imageDisplayWidth);
                        });
        richEditor.getSettings().setAllowFileAccess(true);
        richEditor.getSettings().setAllowFileAccessFromFileURLs(true);

        return root;
    }

    private void setOnClickListeners(View root) {
        root.findViewById(R.id.camera_button)
                .setOnClickListener(
                        v -> {
                            // Open the file picker limiting selections to image files.
                            headerImagePickerActivityLauncher.launch(IMAGE_MIME_TYPE);
                        });
        root.findViewById(R.id.location_button)
                .setOnClickListener(
                        v -> {
                            // Go to the map view.
                            Navigation.findNavController(getView()).navigate(R.id.nav_map);
                        });
        root.findViewById(R.id.audio_button)
                .setOnClickListener(
                        v -> {
                            Navigation.findNavController(getView()).navigate(R.id.nav_audio);
                        });

        root.findViewById(R.id.editor_action_drawing_btn)
                .setOnClickListener(
                        v -> {
                            // Go to the drawing view.
                            Navigation.findNavController(getView()).navigate(R.id.nav_drawing);
                        });
        // Add a click listener to the "back" button to return to the previous activity.
        root.findViewById(R.id.back_button)
                .setOnClickListener((view) -> getParentFragmentManager().popBackStack());
        // Add a click listener to the "save" button to store the changes in the database.
        root.findViewById(R.id.notedisplay_save_btn).setOnClickListener(this::saveNote);

        root.findViewById(R.id.editor_action_bold_btn)
                .setOnClickListener(v -> richEditor.setBold());
        root.findViewById(R.id.editor_action_italic_btn)
                .setOnClickListener(v -> richEditor.setItalic());
        root.findViewById(R.id.editor_action_underline_btn)
                .setOnClickListener(v -> richEditor.setUnderline());
        root.findViewById(R.id.editor_action_strikethrough_btn)
                .setOnClickListener(v -> richEditor.setStrikeThrough());
        root.findViewById(R.id.editor_action_ul_btn)
                .setOnClickListener(v -> richEditor.setBullets());
        root.findViewById(R.id.editor_action_ol_btn)
                .setOnClickListener(v -> richEditor.setNumbers());
        root.findViewById(R.id.editor_action_image_btn)
                .setOnClickListener(
                        v -> embeddedImagePickerActivityLauncher.launch(IMAGE_MIME_TYPE));
    }

    private void updateHeaderImage(Uri uri) {
        ConstraintLayout header = getView().findViewById(R.id.note_header);
        Bitmap bitmap = null;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: could not display the image", Toast.LENGTH_LONG)
                    .show();
        }
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
        header.setBackgroundTintList(null);
        header.setBackground(ob);
    }

    @Override
    public void onPause() {
        super.onPause();
        position = null;
        locationName = null;
    }

    /** Save the displayed note in the database. */
    // TODO: Use the MVVM pattern here as well, so that the ViewModel is updated right after
    //  making the changes (instead of manually "saving" the note when the button is pressed). This
    //  would also remove the need to have a "save" button (because the changes would be reflected
    //  in real time in the ViewModel and thus in the database).
    private void saveNote(View view) {
        database.getNote(noteID)
                .thenCompose(
                        note -> {
                            TextView noteTitle = getView().findViewById(R.id.note_title);
                            note.setTitle(noteTitle.getText().toString());
                            note.setContent(richEditor.getHtml().trim());
                            return database.putNote(noteID, note);
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: Update the note's location via a ViewModel, so that no static attributes have to
        //  be used at all for passing data between the MapFragment and the NoteDisplayFragment.
        if (position != null && locationName != null) setLocationNote(position, locationName);
        if (lastDrawingUri != null) {
            richEditor.focusEditor();
            richEditor.insertImage(lastDrawingUri, "", imageDisplayWidth);
            lastDrawingUri = null;
        }
    }

    public void setLocationNote(LatLng latLng, String location) {
        // TODO : Change the location of the note when the activity will be link with real note
        if (latLng != null && location != null) {
            TextView locationText = getView().findViewById(R.id.note_location);
            locationText.setText(location);
        }
    }
}
