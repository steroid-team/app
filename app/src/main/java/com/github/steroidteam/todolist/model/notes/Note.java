package com.github.steroidteam.todolist.model.notes;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;

import java.util.Optional;
import java.util.UUID;

public class Note {

    private final UUID id;
    private String title;
    private String content;
    private LatLng latLng;
    private UUID audioMemoId;

    public Note(@NonNull String title) {
        if (title == null) {
            throw new IllegalArgumentException();
        }
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = "";
        this.latLng = null;
        this.audioMemoId = null;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Optional<UUID> getAudioMemoId() {
        if (audioMemoId == null) return Optional.empty();
        else return Optional.of(audioMemoId);
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public void setLatLng(@NonNull LatLng latLng) {
        this.latLng = latLng;
    }

    public void setAudioMemoId(@NonNull UUID audioMemoId) {
        this.audioMemoId = audioMemoId;
    }

    public void removeAudioMemoId() {
        audioMemoId = null;
    }
}
