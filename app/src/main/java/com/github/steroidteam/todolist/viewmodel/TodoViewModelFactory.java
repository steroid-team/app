package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.github.steroidteam.todolist.database.TodoListRepository;
import org.jetbrains.annotations.NotNull;

public class TodoViewModelFactory implements ViewModelProvider.Factory {

    private final TodoListRepository todoTodoListRepository;

    public TodoViewModelFactory(TodoListRepository todoTodoListRepository) {
        this.todoTodoListRepository = todoTodoListRepository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TodoListViewModel.class)) {
            return (T) new TodoListViewModel(todoTodoListRepository);
        }
        throw new IllegalArgumentException("ViewModel Unknown");
    }
}
