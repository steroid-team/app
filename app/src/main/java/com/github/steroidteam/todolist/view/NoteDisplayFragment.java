package com.github.steroidteam.todolist.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.google.android.gms.maps.model.LatLng;
import java.io.InputStream;
import java.util.UUID;

public class NoteDisplayFragment extends Fragment {
    public static LatLng position;
    public static String locationName;
    private ActivityResultLauncher<String> headerImagePickerActivityLauncher;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_display, container, false);

        root.findViewById(R.id.camera_button)
                .setOnClickListener(
                        v -> {
                            // Open the file picker limiting selections to image files.
                            headerImagePickerActivityLauncher.launch("image/*");
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

        Database database = DatabaseFactory.getDb();

        UUID id = UUID.fromString(getArguments().getString(NoteSelectionFragment.NOTE_ID_KEY));
        EditText editText = root.findViewById(R.id.activity_notedisplay_edittext);

        database.getNote(id)
                .thenAccept(
                        note -> {
                            TextView noteTitle = root.findViewById(R.id.note_title);
                            noteTitle.setText(note.getTitle());

                            editText.setText(note.getContent());
                        });

        headerImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(), this::updateHeaderImage);

        return root;
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
    public void onDestroyView() {
        super.onDestroyView();
        position = null;
        locationName = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: Update the note's location via a ViewModel, so that no static attributes have to
        //  be used at all for passing data between the MapFragment and the NoteDisplayFragment.
        if (position != null && locationName != null) setLocationNote(position, locationName);
    }

    public void setLocationNote(LatLng latLng, String location) {
        // TODO : Change the location of the note when the activity will be link with real note
        if (latLng != null && location != null) {
            TextView locationText = getView().findViewById(R.id.note_location);
            locationText.setText(location);
        }
    }
}
