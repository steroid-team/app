package com.github.steroidteam.todolist.view;

import android.database.Cursor;
import static com.github.steroidteam.todolist.view.NoteSelectionFragment.NOTE_ID_KEY;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.os.Environment;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.util.ViewUtils;
import com.github.steroidteam.todolist.viewmodel.NoteViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.view.dialog.ListSelectionDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import javax.xml.transform.URIResolver;

import jp.wasabeef.richeditor.RichEditor;

public class NoteDisplayFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private View root;

    private UUID noteID;

    private RichEditor richEditor;
    private Uri cameraFileUri;
    private ActivityResultLauncher<String> headerImagePickerActivityLauncher;
    private ActivityResultLauncher<String> embeddedImagePickerActivityLauncher;
    private ActivityResultLauncher<Uri> cameraActivityLauncher;
    private final String IMAGE_MIME_TYPE = "image/*";
    int imageDisplayWidth;

    public static final String MAP_FRAGMENT_REQUEST_KEY = "MAP_FRAGMENT_REQUEST_KEY";
    public static final String MAP_POSITION_KEY = "MAP_POSITION_KEY";
    public static final String MAP_LOCATION_NAME_KEY = "MAP_LOCATION_NAME_KEY";

    private String imageFileName;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = inflater.inflate(R.layout.fragment_note_display, container, false);

        imageFileName = getActivity().getExternalCacheDir().getAbsolutePath() + "/image.png";

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

        // Set view model and observe the note:
        this.noteViewModel = new NoteViewModel(noteID);
        this.noteViewModel.getNote().observe(getViewLifecycleOwner(), this::updateUI);
        this.noteViewModel.getHeaderID().observe(getViewLifecycleOwner(), this::updateUIHeader);

        // This methods need the view model:
        setMapFragmentListener();
        setRichEditorListeners(root);

        File file = new File(Environment.getExternalStorageDirectory(), "picFromCamera");
        cameraFileUri =
                FileProvider.getUriForFile(
                        getContext(),
                        "com.asteroid.fileprovider",
                        getPhotoFileUri("camera-img.jpg"));

        headerImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(), this::updateHeaderImage);
        cameraActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.TakePicture(),
                        success -> {
                            if (success) updateHeaderImage(cameraFileUri);
                        });
        embeddedImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null)
                                richEditor.insertImage(uri.toString(), "", imageDisplayWidth);
                        });

        return root;
    }

    // UPDATE THE UI:
    // Only the content, title, ... everything that isn't too big.
    private void updateUI(Note note) {
        TextView noteTitle = root.findViewById(R.id.note_title);
        noteTitle.setText(note.getTitle());
        richEditor.setHtml(note.getContent());
        TextView locationText = root.findViewById(R.id.note_location);
        locationText.setText(note.getLocationName());
    }

    // ONLY UPDATE THE HEADER OF THE NOTE
    // There are two different methods,
    // So when you update the content you don't re-download the picture.
    private void updateUIHeader(Optional<UUID> headerID) {
        headerID.ifPresent(uuid -> {
            // Download the image in the local file system
            noteViewModel.getNoteHeader(uuid, imageFileName)
                    .thenAccept(file -> {
                        ConstraintLayout header = getView().findViewById(R.id.note_header);
                        // Set the image on the header of the note
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                        header.setBackgroundTintList(null);
                        header.setBackground(ob);
                    });
        });
    }

    private void storeUserChanges() {
        noteViewModel.updateNoteContent(richEditor.getHtml().trim());
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

        // Store the new image in the database:
        try {
            noteViewModel.updateNoteHeader(getRealPathFromURI(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContext().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void setMapFragmentListener() {
        getParentFragmentManager()
                .setFragmentResultListener(
                        MAP_FRAGMENT_REQUEST_KEY,
                        this,
                        (requestKey, result) -> {
                            LatLng position = result.getParcelable(MAP_POSITION_KEY);
                            String locationName = result.getString(MAP_LOCATION_NAME_KEY);

                            noteViewModel.setPositionAndLocation(position, locationName);
                        });
    }

    private void setRichEditorListeners(View root) {
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

        richEditor.setFocusable(true);
        richEditor.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        storeUserChanges();
                        ViewUtils.hideKeyboard(root);
                    }
                });
    }

    private void setOnClickListeners(View root) {
        root.findViewById(R.id.camera_button)
                .setOnClickListener(
                        v -> {
                            DialogInterface.OnClickListener listener =
                                    (dialog, position) -> {
                                        switch (position) {
                                            case 0:
                                                headerImagePickerActivityLauncher.launch(
                                                        IMAGE_MIME_TYPE);
                                                break;
                                            case 1:
                                                cameraActivityLauncher.launch(cameraFileUri);
                                                break;
                                        }
                                    };
                            DialogFragment newFragment =
                                    new ListSelectionDialogFragment()
                                            .newInstance(
                                                    listener,
                                                    R.string.add_image_dialog,
                                                    R.array.add_image_types);
                            newFragment.show(getParentFragmentManager(), "pick_dialog");
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
                            Bundle bundle = new Bundle();
                            bundle.putString(NOTE_ID_KEY, noteID.toString());
                            Navigation.findNavController(getView())
                                    .navigate(R.id.nav_audio, bundle);
                        });

        root.findViewById(R.id.editor_action_drawing_btn)
                .setOnClickListener(
                        v -> {
                            // Go to the drawing view.
                            Navigation.findNavController(getView()).navigate(R.id.nav_drawing);
                        });
        // Add a click listener to the "back" button to return to the previous activity.
        root.findViewById(R.id.back_button)
                .setOnClickListener(
                        (view) -> {
                            storeUserChanges();
                            getParentFragmentManager().popBackStack();
                        v -> embeddedImagePickerActivityLauncher.launch(IMAGE_MIME_TYPE));
    }

    private void updateHeaderImage(Uri uri) {
        if (uri == null) return;
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

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir =
                new File(getContext().getExternalFilesDir(null), "asteroid-notes/images");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("asteroid-notes", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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
}
