package com.github.steroidteam.todolist.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class AudioRecorderFragment extends Fragment {

    private View root;
    private String audioFileName;
    // Maybe change that later to have multiple audios
    private String testFileName = "/audioTest.3gp";

    private static final int AUDIO_PERMISSION = 200;

    private boolean isPermissionGiven = false;

    private boolean isRecording = false;
    private MediaRecorder recorder;

    private boolean isFirstTime = true;
    private MediaPlayer player;

    private Database database;
    private UUID noteId;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_audio_recorder, container, false);
        root.findViewById(R.id.back_button)
                .setOnClickListener(v -> getParentFragmentManager().popBackStack());
        root.findViewById(R.id.record_button).setOnClickListener(this::recordOnClick);

        audioFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        audioFileName += testFileName;

        player = new MediaPlayer();
        player.setOnCompletionListener(player -> onCompletePlaying());
        ActivityCompat.requestPermissions(
                getActivity(), new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION);

        database = DatabaseFactory.getDb();
        noteId = UUID.fromString(getArguments().getString(NoteSelectionFragment.NOTE_ID_KEY));

        /* The play button remains unclickable until there is an audio file to play */
        root.findViewById(R.id.play_button).setOnClickListener(this::playingOnClick);
        showProgressBar(true);

        database.getNote(noteId)
                .thenAccept(
                        note -> {
                            Optional<UUID> audioID = note.getAudioMemoId();

                            audioID.ifPresent(
                                    uuid ->
                                            database.getAudioMemo(uuid, audioFileName)
                                                    .thenAccept(file -> showProgressBar(false)));

                            if (!audioID.isPresent()) showProgressBar(false);
                        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        showProgressBar(true);
        try {
            database.setAudioMemo(noteId, audioFileName)
                    .thenAccept(
                            nothing -> {
                                showProgressBar(false);
                            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showProgressBar(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AUDIO_PERMISSION:
                isPermissionGiven = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!isPermissionGiven) {
            getActivity().finish();
        }
    }

    public void recordOnClick(View view) {
        if (isRecording) {
            TextView textView = root.findViewById(R.id.record_text);
            stopRecording();
            textView.setText(getText(R.string.record_button));
            isRecording = false;
        } else {
            startRecording();
            isRecording = true;
            TextView textView = root.findViewById(R.id.record_text);
            textView.setText(getText(R.string.stop_record_button));
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(audioFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        Toast.makeText(getContext(), getString(R.string.is_recording), Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Toast.makeText(getContext(), getString(R.string.stop_recording), Toast.LENGTH_SHORT).show();
    }

    private void onCompletePlaying() {
        ImageButton playButton = root.findViewById(R.id.play_button);
        playButton.setImageResource(android.R.drawable.ic_media_play);
        TextView textView = root.findViewById(R.id.play_text);
        textView.setText(getText(R.string.play_button));
    }

    public void playingOnClick(View view) {
        if (player.isPlaying()) {
            pausePlayingAudio();
            TextView textView = root.findViewById(R.id.play_text);
            textView.setText(getText(R.string.play_button));
        } else {
            playingAudio();
            TextView textView = root.findViewById(R.id.play_text);
            textView.setText(getText(R.string.pause_button));
        }
    }

    private void pausePlayingAudio() {
        player.pause();
        ImageButton playButton = root.findViewById(R.id.play_button);
        playButton.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playingAudio() {
        try {
            if (isFirstTime) {
                player.setDataSource(audioFileName);
                player.prepare();
                isFirstTime = false;
            }
            player.reset();
            player.setDataSource(audioFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageButton playButton = root.findViewById(R.id.play_button);
        playButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void showProgressBar(boolean show) {
        root.findViewById(R.id.play_button).setClickable(!show);
        root.findViewById(R.id.play_button).setAlpha(show ? 0.5f : 1f);
        root.findViewById(R.id.audioTransferProgress)
                .setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
