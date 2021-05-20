package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.github.steroidteam.todolist.database.NoteRepository;
import org.jetbrains.annotations.NotNull;

public class NoteViewModelFactory implements ViewModelProvider.Factory {
    private final NoteRepository noteRepository;

    public NoteViewModelFactory(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NoteViewModel.class)) {
            return (T) new NoteViewModel(noteRepository);
        }
        throw new IllegalArgumentException("ViewModel Unknown");
    }
}
