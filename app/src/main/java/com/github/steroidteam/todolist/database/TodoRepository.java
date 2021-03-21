package com.github.steroidteam.todolist.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

public class TodoRepository {

    private VolatileDatabase database;
    private MutableLiveData<TodoList> oneTodoList;

    // ====== TO DELETE ========
    // We don't have persistent database !
    public UUID id;
    // =========================


    public TodoRepository() {
        this.database = new VolatileDatabase();

        // ====== TO DELETE ========
        // We don't have persistent database !
        TodoList tl = new TodoList("A Todo!");
        this.database.putTodoList(tl);
        id = tl.getId();
        System.out.println("IN REPO : " + id);
        System.err.println("id " + tl.getId());
        // =========================
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        if(oneTodoList==null) {
            oneTodoList = new MutableLiveData<TodoList>(this.database.getTodoList(todoListID));
        }
        return this.oneTodoList;
    }

    public void putTask(UUID todoListID, Task task) {
        this.database.putTask(todoListID, task);
        System.out.println("new value : " + this.database.getTodoList(todoListID));
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }

    public void removeTask(UUID todoListID, int index) {
        this.database.removeTask(todoListID, index);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }
}
