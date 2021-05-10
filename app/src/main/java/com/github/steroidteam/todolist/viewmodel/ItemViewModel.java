package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.database.TodoListRepository;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.Date;

public class ItemViewModel extends ViewModel {

    private final TodoListRepository repository;
    private final LiveData<TodoList> todoList;

    public ItemViewModel(@NonNull TodoListRepository repository) {
        super();
        this.repository = repository;
        todoList = repository.getTodoList();
    }

    public LiveData<TodoList> getTodoList() {
        return this.todoList;
    }

    public void addTask(Task task) {
        repository.putTask(task);
    }

    public void removeTask(int index) {
        repository.removeTask(index);
    }

    public void renameTask(int index, String newBody) {
        if (!newBody.equals("")) {
            repository.renameTask(index, newBody);
        }
    }

    public void setTaskDone(int index, boolean isDone) {
        repository.setTaskDone(index, isDone);
    }

    public void setTaskDueDate(int index, Date date) {
        repository.setTaskDueDate(index, date);
    }
}
