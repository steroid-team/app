package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.database.TodoListRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

public class ListSelectionViewModel extends ViewModel {

    private final TodoListRepository todoListRepository;
    private final LiveData<ArrayList<TodoList>> arrayOfTodoList;

    public ListSelectionViewModel(@NonNull TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
        this.arrayOfTodoList = todoListRepository.getAllTodo();
    }

    public LiveData<ArrayList<TodoList>> getListOfTodo() {
        return this.arrayOfTodoList;
    }

    public void addTodo(String title) {
        todoListRepository.putTodo(new TodoList(title));
    }

    public void removeTodo(UUID id) {
        todoListRepository.removeTodo(id);
    }

    public void renameTodo(UUID id, TodoList todoListUpdated) {
        todoListRepository.renameTodo(id, todoListUpdated);
    }
}
