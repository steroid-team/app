package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.google.android.gms.maps.model.LatLng;
import java.util.UUID;

public class NoteViewModel extends ViewModel {

    private final Database database;
    private final MutableLiveData<Note> noteMutableLiveData;
    private final UUID noteID;

    public NoteViewModel(UUID noteID) {
        super();
        this.database = DatabaseFactory.getDb();
        this.noteMutableLiveData = new MutableLiveData<>();
        this.noteID = noteID;
        // this.noteMutableLiveData.setValue(new Note("Placeholder"));
        this.database.getNote(noteID).thenAccept(noteMutableLiveData::setValue);
    }

    public LiveData<Note> getNote() {
        return this.noteMutableLiveData;
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
}
