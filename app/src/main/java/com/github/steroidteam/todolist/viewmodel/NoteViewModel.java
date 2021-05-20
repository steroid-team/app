package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.NoteRepository;
import com.github.steroidteam.todolist.model.notes.Note;
import java.util.ArrayList;
import java.util.UUID;

public class NoteViewModel extends ViewModel {

    private final NoteRepository noteRepository;

    private final LiveData<ArrayList<Note>> allNote;

    public NoteViewModel(@NonNull NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.allNote = noteRepository.getAllNote();
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

    public void renameNote(Note note, String newTitle) {
        this.noteRepository.updateNote(note.getId(), note.setTitle(newTitle));
    }
}
