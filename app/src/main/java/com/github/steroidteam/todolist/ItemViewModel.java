package com.github.steroidteam.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.github.steroidteam.todolist.database.TodoRepository;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

public class ItemViewModel extends AndroidViewModel {

    private TodoRepository repository;
    private LiveData<TodoList> todoList;
    private UUID todoListID;

    public ItemViewModel(@NonNull Application application, UUID todoListID) {
        super(application);
        repository = new TodoRepository();

        // ====== TO DELETE ========
        // We don't have persistent database !
        this.todoListID = repository.id;
        // =========================
        // this.todoListID = todoListID;
        //System.err.println("On a : " + this.todoListID);
        this.repository.putTask(todoListID, new Task("Test"));
        todoList = repository.getTodoList(this.todoListID);
    }


    public LiveData<TodoList> getTodoList() {
        return this.todoList;
    }

    public void addTask(String body) {
        repository.putTask(todoListID, new Task(body));
    }

    public void removeTask(int index) {
        repository.removeTask(todoListID, index);
    }
}