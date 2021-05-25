package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.database.NoteRepository;
import com.github.steroidteam.todolist.model.notes.Note;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NoteViewModel extends ViewModel {

    private final NoteRepository noteRepository;

    private final LiveData<ArrayList<Note>> allNote;
    private final LiveData<Note> noteSelected;

    private UUID selectedNoteID;

    public NoteViewModel(@NonNull NoteRepository noteRepository) {
        super();
        this.noteRepository = noteRepository;

        this.allNote = noteRepository.getAllNote();
        this.noteSelected = noteRepository.getNote();
    }

    public void selectNote(UUID id) {
        this.selectedNoteID = id;
        noteRepository.selectNote(id);
    }

    public LiveData<Note> getNote() {
        return this.noteSelected;
    }

    public LiveData<ArrayList<Note>> getNoteList() {
        return this.allNote;
    }

    public void putNote(String title) {
        Note newNote = new Note(title);
        this.noteRepository.putNote(newNote);
    }

    public void removeNote(UUID noteID) {
        this.noteRepository.removeNote(noteID);
    }

    public void renameNote(UUID noteID, Note updatedNote) {
        this.noteRepository.updateNote(noteID, updatedNote);
    }

    public void updateNoteContent(String content) {
        this.noteRepository.updateNote(selectedNoteID, noteSelected.getValue().setContent(content));
    }

    public void setPositionAndLocation(LatLng position, String locationName) {
        if (position != null && locationName != null) {
            this.noteRepository.updateNote(selectedNoteID, noteSelected
                    .getValue()
                    .setLatLng(position)
                    .setLocationName(locationName));
        }
    }

    public void updateNoteHeader(String imageFileName) throws FileNotFoundException {
        this.noteRepository
                .setHeaderNote(selectedNoteID, imageFileName);
    }

    public CompletableFuture<File> getNoteHeader(UUID imageID, String destinationPath) {
        return this.noteRepository
                .getNoteHeader(imageID, destinationPath);
    }
}
