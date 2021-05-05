package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.model.TodoArrayRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.List;
import java.util.UUID;

public class ListSelectionViewModel extends ViewModel {

    private final TodoArrayRepository repository;
    private final LiveData<List<TodoList>> arrayOfTodoList;

    public ListSelectionViewModel(@NonNull TodoArrayRepository repository) {
        super();
        this.repository = repository;
        this.arrayOfTodoList = repository.getAllTodo();
    }

    public LiveData<List<TodoList>> getListOfTodo() {
        return this.arrayOfTodoList;
    }

    public void addTodo(String title) {
        repository.putTodo(new TodoList(title));
    }

    public void removeTodo(UUID id) {
        repository.removeTodo(id);
    }

    public void renameTodo(TodoList todoList, String newTitle) {
        repository.renameTodo(todoList.getId(), todoList.setTitle(newTitle));
    }
}
