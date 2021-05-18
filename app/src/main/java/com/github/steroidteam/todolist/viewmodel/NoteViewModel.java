package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NoteViewModel extends ViewModel {

    private final Database database;
    private final MutableLiveData<Note> noteMutableLiveData;
    private final MutableLiveData<Optional<UUID>> headerIDMutableLiveData;
    private final UUID noteID;

    public NoteViewModel(UUID noteID) {
        super();
        this.database = DatabaseFactory.getDb();
        this.noteMutableLiveData = new MutableLiveData<>();
        this.headerIDMutableLiveData = new MutableLiveData<>();
        this.noteID = noteID;
        // this.noteMutableLiveData.setValue(new Note("Placeholder"));
        this.database.getNote(noteID).thenAccept(this::setLiveData);
    }

    private void setLiveData(Note note) {
        this.noteMutableLiveData.postValue(note);
        this.headerIDMutableLiveData.postValue(note.getHeaderID());
    }

    public LiveData<Note> getNote() {
        return this.noteMutableLiveData;
    }

    public LiveData<Optional<UUID>> getHeaderID() {
        return this.headerIDMutableLiveData;
    }

    public void updateNoteContent(String content) {
        this.database
                .updateNote(noteID, noteMutableLiveData.getValue().setContent(content))
                .thenCompose(note -> this.database.getNote(noteID))
                .thenAccept(noteMutableLiveData::setValue);
    }

    public void setPositionAndLocation(LatLng position, String locationName) {
        if (position != null && locationName != null) {
            this.database
                    .updateNote(
                            noteID,
                            noteMutableLiveData
                                    .getValue()
                                    .setLatLng(position)
                                    .setLocationName(locationName))
                    .thenCompose(note -> this.database.getNote(noteID))
                    .thenAccept(noteMutableLiveData::setValue);
        }
    }

    public void updateNoteHeader(String imageFileName) throws FileNotFoundException {
        this.database
                .setHeaderNote(noteID, imageFileName)
                .thenCompose(note -> this.database.getNote(noteID))
                .thenAccept(this::setLiveData);
    }

    public CompletableFuture<File> getNoteHeader(UUID imageID, String destinationPath) {
        return this.database.getImage(imageID, destinationPath);
    }
}
