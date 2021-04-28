package com.github.steroidteam.todolist.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.github.steroidteam.todolist.R;
import java.io.IOException;

public class AudioRecorderActivity extends AppCompatActivity {
    private String fileName;
    private static final int AUDIO_PERMISSION = 200;

    private boolean isPermissionGiven = false;

    private boolean isRecording = false;
    private MediaRecorder recorder;

    private boolean isPlaying = false;
    private boolean isFirstTime = true;
    private MediaPlayer player;
    private int currPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> finish());
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audioTest.3gp";

        player = new MediaPlayer();

        ActivityCompat.requestPermissions(
                this, new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION);
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
            finish();
        }
    }

    public void recordOnClick(View view) {
        if (isRecording) {
            stopRecording();
            isRecording = false;
            TextView textView = findViewById(R.id.record_text);
            textView.setText("Record");
        } else {
            startRecording();
            isRecording = true;
            TextView textView = findViewById(R.id.record_text);
            textView.setText("Stop recording");
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.is_recording),
                        Toast.LENGTH_SHORT)
                .show();
    }

    private void pauseRecording() {
        recorder.pause();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.stop_recording),
                        Toast.LENGTH_SHORT)
                .show();
    }

    public void playingOnClick(View view) {
        if (isPlaying) {
            pausePlayingAudio();
            isPlaying = false;
            TextView textView = findViewById(R.id.play_text);
            textView.setText("Play");
        } else {
            playingAudio();
            isPlaying = true;
            TextView textView = findViewById(R.id.play_text);
            textView.setText("Pause");
        }
    }

    private void pausePlayingAudio() {
        player.pause();
        currPos = player.getCurrentPosition();
        ImageButton playButton = findViewById(R.id.play_button);
        playButton.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playingAudio() {
        try {
            if (isFirstTime) {
                player.setDataSource(fileName);
                player.prepare();
                isFirstTime = false;
            }
            //player.seekTo(currPos);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageButton playButton = findViewById(R.id.play_button);
        playButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void stopPlayingAudio() {
        player.release();
        player = null;
    }
}
