package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NoteSelectionViewModel extends ViewModel {

    private final Database database;
    private final MutableLiveData<List<Note>> noteList;

    public NoteSelectionViewModel() {
        super();
        this.database = DatabaseFactory.getDb();
        this.noteList = new MutableLiveData<>(new ArrayList<>());
        this.database.getNotesList().thenAccept(this::setArrayOfNote);
    }

    public LiveData<List<Note>> getNoteList() {
        return this.noteList;
    }

    private void setArrayOfNote(List<UUID> noteIDList) {
        ArrayList<Note> privateArrayList = new ArrayList<>();
        if(noteIDList.size()==0) {
            noteList.setValue(privateArrayList);
        }
        else {
            for (int i = 0; i < noteIDList.size(); i++) {
                this.database
                        .getNote(noteIDList.get(i))
                        .thenAccept(
                                note -> {
                                    privateArrayList.add(note);
                                    noteList.setValue(privateArrayList);
                                });
            }
        }
    }

    public void putNote(String title) {
        Note newNote = new Note(title);
        this.database
                .putNote(newNote.getId(), newNote)
                .thenCompose(str -> this.database.getNotesList())
                .thenAccept(this::setArrayOfNote);
    }

    public void removeNote(UUID noteID) {
        this.database
                .removeNote(noteID)
                .thenCompose(str -> this.database.getNotesList())
                .thenAccept(this::setArrayOfNote);
    }

    public void renameNote(Note note, String newTitle) {
        this.database
                .updateNote(note.getId(), note.setTitle(newTitle))
                .thenCompose(str -> this.database.getNotesList())
                .thenAccept(this::setArrayOfNote);
    }
}
