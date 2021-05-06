package com.github.steroidteam.todolist.model.notes;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import java.util.Objects;
import java.util.UUID;

public class Note {

    private final UUID id;
    private String title;
    private String content;
    private LatLng latLng;
    private String locationName;

    private Bitmap header;

    private static final String NO_LOCATION = "No location";

    public Note(@NonNull String title) {
        if (title == null) {
            throw new IllegalArgumentException();
        }
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = "";
        this.latLng = null;
        this.locationName = NO_LOCATION;
        this.header = null;
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

    public Note setTitle(@NonNull String title) {
        this.title = title;
        return this;
    }

    public Note setContent(@NonNull String content) {
        this.content = content;
        return this;
    }

    public Note setLatLng(@NonNull LatLng latLng) {
        Objects.requireNonNull(latLng);
        this.latLng = latLng;
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public Note setLocationName(String locationName) {
        Objects.requireNonNull(locationName);
        this.locationName = locationName;
        return this;
    }

    public Bitmap getHeader() {
        return header;
    }

    public void setHeader(Bitmap header) {
        this.header = header;
    }
}
