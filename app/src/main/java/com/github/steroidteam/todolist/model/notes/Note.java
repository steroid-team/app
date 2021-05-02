package com.github.steroidteam.todolist.model.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import java.util.UUID;

public class Note {

    private final UUID id;
    private String title;
    private String content;
    private LatLng latLng;

    public Note(@NonNull String title) {
        if (title == null) {
            throw new IllegalArgumentException();
        }
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = "";
        this.latLng = null;
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

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public void setLatLng(@NonNull LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "Note{\n" + "title='" + title + "',\n" + "content='" + content + "',\n" + "coordinates='" + latLng.toString() + "'\n}";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return note.getId().equals(this.getId());
    }
}
