package com.github.steroidteam.todolist.view;

import static com.github.steroidteam.todolist.util.Utils.dip2px;
import static com.github.steroidteam.todolist.view.NoteSelectionFragment.NOTE_ID_KEY;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.util.Utils;
import com.github.steroidteam.todolist.view.dialog.ListSelectionDialogFragment;
import com.github.steroidteam.todolist.viewmodel.NoteViewModel;
import com.github.steroidteam.todolist.viewmodel.NoteViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import jp.wasabeef.richeditor.RichEditor;

public class NoteDisplayFragment extends Fragment {
    public static LatLng position;
    public static String locationName;

    private UUID noteID;
    private RichEditor richEditor;
    private Uri cameraFileUri;
    private ActivityResultLauncher<String> headerImagePickerActivityLauncher;
    private ActivityResultLauncher<String> embeddedImagePickerActivityLauncher;
    private ActivityResultLauncher<Uri> cameraActivityLauncher;
    private final String IMAGE_MIME_TYPE = "image/*";

    int imageDisplayWidth;

    private NoteViewModel noteViewModel;

    private String headerFileName;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_display, container, false);

        headerFileName = getActivity().getCacheDir().getAbsolutePath();

        initRichEditor(root);

        setNavOnClickListeners(root);
        setImagePickerListeners(root);
        setRichEditorListeners(root);

        NoteViewModelFactory noteViewModelFactory =
                ViewModelFactoryInjection.getNoteViewModelFactory(getContext());
        this.noteViewModel =
                new ViewModelProvider(requireActivity(), noteViewModelFactory)
                        .get(NoteViewModel.class);

        this.noteViewModel
                .getNote()
                .observe(
                        getViewLifecycleOwner(),
                        note -> {
                            System.out.println("NOTEEE ID : " + note.getId());
                            if (position != null && locationName != null) {
                                this.noteViewModel.setPositionAndLocation(position, locationName);
                                position = null;
                                locationName = null;
                            }
                            updateHeader(note.getHeaderID());
                            updateUI(root, note);
                        });

        setActivityLauncher();

        return root;
    }

    private void updateHeader(Optional<UUID> optionalUUID) {
        ConstraintLayout header = getView().findViewById(R.id.note_header);

        if(optionalUUID.isPresent()) {

            File imagePath =
                    new File(
                            headerFileName,
                            "user-data/" + UserFactory.get().getUid() + "/images/");
            noteViewModel
                    .getNoteHeader(optionalUUID.get(), imagePath.getAbsolutePath())
                    .thenAccept(
                            (f) -> {
                                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                                BitmapDrawable ob =
                                        new BitmapDrawable(getResources(), getRoundedBitmap(bitmap));
                                header.setBackgroundTintList(null);
                                header.setBackground(ob);
                            });
        } else {
            header.setBackgroundTintList(ColorStateList.valueOf(getActivity().getColor(R.color.light_grey)));
            header.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corner_just_bottom_bg));
        }
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = dip2px(getContext(), 50);
        paint.setAntiAlias(true);
        Path path = new Path();
        float[] corners =
                new float[] {
                    0,
                    0, // Top left radius in px
                    0,
                    0, // Top right radius in px
                    roundPx,
                    roundPx, // Bottom right radius in px
                    roundPx,
                    roundPx // Bottom left radius in px
                };
        path.addRoundRect(rectF, corners, Path.Direction.CW);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void updateUI(View root, Note note) {
        // TITLE
        TextView noteTitle = root.findViewById(R.id.note_title);
        noteTitle.setText(note.getTitle());

        // RICH EDITOR
        richEditor.setHtml(note.getContent());

        // LOCATION
        TextView locationText = root.findViewById(R.id.note_location);
        locationText.setText(note.getLocationName());
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
                        noteViewModel.updateNoteContent(richEditor.getHtml().trim());
                        Utils.hideKeyboard(root);
                    }
                });
    }

    private void initRichEditor(View root) {
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
    }

    private void setImagePickerListeners(View root) {
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
    }

    private void setNavOnClickListeners(View root) {
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
                            bundle.putString(NOTE_ID_KEY, noteViewModel.getNote().getValue().getId().toString());
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
                            // Save changes:
                            noteViewModel.updateNoteContent(richEditor.getHtml().trim());
                            // Return to note selection fragment:
                            getParentFragmentManager().popBackStack();
                        });
    }

    private void setActivityLauncher() {
        File file = new File(Environment.getExternalStorageDirectory(), "picFromCamera");

        // Pick Image from file
        headerImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(), this::updateHeaderImage);

        // Pick Image from camera
        cameraFileUri =
                FileProvider.getUriForFile(
                        getContext(),
                        "com.asteroid.fileprovider",
                        getPhotoFileUri("camera-img.jpg"));
        cameraActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.TakePicture(),
                        success -> {
                            if (success) updateHeaderImage(cameraFileUri);
                        });

        // Insert image in the rich editor
        embeddedImagePickerActivityLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null)
                                richEditor.insertImage(uri.toString(), "", imageDisplayWidth);
                        });
    }

    private void updateHeaderImage(Uri uri) {
        if (uri == null) return;

        Bitmap bitmap;

        String tmpFileName = "bitmap_tmp.jpeg";
        File tmpFile =
                new File(
                        getContext().getCacheDir(),
                        tmpFileName);

        System.err.println(tmpFile.toString() + " " + tmpFile.toURI() + "                     zqdqzd" );
        try (FileOutputStream output = new FileOutputStream(tmpFile)) {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
            is.close();
            output.close();

            noteViewModel.updateNoteHeader(tmpFile.getAbsolutePath());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: could not display the image", Toast.LENGTH_LONG)
                    .show();
        }
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
}
