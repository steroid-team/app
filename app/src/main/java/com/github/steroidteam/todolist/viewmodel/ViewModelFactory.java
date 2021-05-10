package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.steroidteam.todolist.database.TodoListRepository;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final TodoListRepository todoTodoListRepository;

    public ViewModelFactory(TodoListRepository todoTodoListRepository) {
        this.todoTodoListRepository = todoTodoListRepository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ListSelectionViewModel.class)) {
            return (T) new ListSelectionViewModel(todoTodoListRepository);
        }
        if(modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(todoTodoListRepository);
        }
        if(modelClass.isAssignableFrom(TodoListViewModel.class)) {
            return (T) new TodoListViewModel(todoTodoListRepository);
        }
        throw new IllegalArgumentException("ViewModel Unknown");
    }
}
