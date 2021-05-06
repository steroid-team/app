package com.github.steroidteam.todolist.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.util.ViewUtils;
import com.github.steroidteam.todolist.viewmodel.NoteViewModel;
import com.google.android.gms.maps.model.LatLng;
import java.io.InputStream;
import java.util.UUID;
import jp.wasabeef.richeditor.RichEditor;

public class NoteDisplayFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private View root;

    private UUID noteID;

    private RichEditor richEditor;
    private ActivityResultLauncher<String> headerImagePickerActivityLauncher;
    private ActivityResultLauncher<String> embeddedImagePickerActivityLauncher;
    private final String IMAGE_MIME_TYPE = "image/*";

    public static final String MAP_FRAGMENT_REQUEST_KEY = "MAP_FRAGMENT_REQUEST_KEY";
    public static final String MAP_POSITION_KEY = "MAP_POSITION_KEY";
    public static final String MAP_LOCATION_NAME_KEY = "MAP_LOCATION_NAME_KEY";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = inflater.inflate(R.layout.fragment_note_display, container, false);

        setOnClickListeners(root);

        // Rich text editor setup.
        richEditor = root.findViewById(R.id.notedisplay_text_editor);
        richEditor.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_grey));
        int padding = (int) getResources().getDimension(R.dimen.note_body_padding);
        richEditor.setPadding(padding, padding, padding, 0);

        // The width for any embedded image should be the editor's width. Do the math to
        // transform the dp to px, and subtract the lateral padding.
        final int imageDisplayWidth =
                (int)
                                Math.floor(
                                        getResources().getDisplayMetrics().widthPixels
                                                / getResources().getDisplayMetrics().density)
                        - 2 * padding;

        // Get the UUID of the currently selected note.
        noteID = UUID.fromString(getArguments().getString(NoteSelectionFragment.NOTE_ID_KEY));

        // Set view model and observe the note:
        this.noteViewModel = new NoteViewModel(noteID);
        this.noteViewModel.getNote().observe(getViewLifecycleOwner(), this::updateUI);

        // This methods need the view model:
        setMapFragmentListener();
        setRichEditorListeners(root);

        headerImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(), this::updateHeaderImage);
        embeddedImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> richEditor.insertImage(uri.toString(), "", imageDisplayWidth));

        return root;
    }

    private void updateUI(Note note) {
        TextView noteTitle = root.findViewById(R.id.note_title);
        noteTitle.setText(note.getTitle());
        richEditor.setHtml(note.getContent());
        TextView locationText = root.findViewById(R.id.note_location);
        locationText.setText(note.getLocationName());
    }

    private void storeUserChanges() {
        noteViewModel.updateNoteContent(richEditor.getHtml().trim());
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
                .setOnClickListener(
                        (view) -> {
                            storeUserChanges();
                            getParentFragmentManager().popBackStack();
                        });
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
}
