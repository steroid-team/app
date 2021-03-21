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
    public UUID todoListID;

    public ItemViewModel(@NonNull Application application, UUID todoListIDD) {
        super(application);
        repository = new TodoRepository();

        // ====== TO DELETE ========
        // We don't have persistent database !
        this.todoListID = repository.id;
        // =========================
        // We will do this instead :
        // this.todoListID = todoListIDD

        todoList = repository.getTodoList(this.todoListID);
    }


    public LiveData<TodoList> getTodoList() {
        return this.todoList;
    }

    public void addTask(String body) {
        System.out.println("AJOUTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        repository.putTask(todoListID, new Task(body));
    }

    public void removeTask(int index) {
        repository.removeTask(todoListID, index);
    }
}
