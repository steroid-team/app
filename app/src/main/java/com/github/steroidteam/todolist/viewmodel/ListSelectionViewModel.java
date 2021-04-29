package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

public class ListSelectionViewModel extends ViewModel {

    private final TodoRepository repository;
    private final LiveData<ArrayList<TodoList>> arrayOfTodoList;

    public ListSelectionViewModel(@NonNull TodoRepository repository) {
        super();
        this.repository = repository;
        this.arrayOfTodoList = repository.getAllTodo();
    }

    public LiveData<ArrayList<TodoList>> getListOfTodo() {
        return this.arrayOfTodoList;
    }

    public void addTodo(String title) {
        repository.putTodo(new TodoList(title));
    }

    public void removeTodo(UUID id) {
        repository.removeTodo(id);
    }

    public void renameTodo(UUID id, TodoList todoListUpdated) {
        repository.renameTodo(id, todoListUpdated);
    }
}
