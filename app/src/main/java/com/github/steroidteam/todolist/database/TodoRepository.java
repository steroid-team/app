package com.github.steroidteam.todolist.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

public class TodoRepository {

    private VolatileDatabase database;
    private LiveData<TodoList> oneTodoList;

    // ====== TO DELETE ========
    // We don't have persistent database !
    public UUID id;
    // =========================


    public TodoRepository() {
        this.database = new VolatileDatabase();

        // ====== TO DELETE ========
        // We don't have persistent database !
        TodoList tl = new TodoList("OUIIII");
        tl.addTask(new Task("une task"));
        this.database.putTodoList(tl);
        id = tl.getId();
        System.err.println("id " + id);
        // =========================
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        return new MutableLiveData<TodoList>(this.database.getTodoList(todoListID));
    }

    public void putTask(UUID todoListID, Task task) {
        this.database.putTask(todoListID, task);
    }

    public void removeTask(UUID todoListID, int index) {
        this.database.removeTask(todoListID, index);
    }
}
