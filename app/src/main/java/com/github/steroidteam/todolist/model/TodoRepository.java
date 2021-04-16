package com.github.steroidteam.todolist.model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.LocalDatabase;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.UUID;

public class TodoRepository {

    private LocalDatabase database;
    private MutableLiveData<TodoList> oneTodoList;

    // ====== TO DELETE ======== BEGIN
    // We don't have persistent database !
    // So we set a public attribute to gets the ID
    // of the todoList in the item view model.
    public UUID id;
    // ========================= END

    public TodoRepository(Context context) {
        LocalFileStorageService str = new LocalFileStorageService(context);
        this.database = new LocalDatabase(str);

        // ====== TO DELETE ======== BEGIN
        // We don't have persistent database !
        // We need to add a todoList,
        // Otherwise we will get a NullPointerException
        // in the getTodoList method.
        TodoList tl = new TodoList("A Todo!");
        try {
            this.database.putTodoList(tl);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        id = tl.getId();
        // ========================= END
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        if (oneTodoList == null) {
            try {
                oneTodoList = new MutableLiveData<TodoList>(this.database.getTodoList(todoListID));
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
        return this.oneTodoList;
    }

    public void putTask(UUID todoListID, Task task) {
        try {
            this.database.putTask(todoListID, task);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        try {
            this.oneTodoList.setValue(this.database.getTodoList(todoListID));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void removeTask(UUID todoListID, int index) {
        try {
            this.database.removeTask(todoListID, index);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        try {
            this.oneTodoList.setValue(this.database.getTodoList(todoListID));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void renameTask(UUID todoListID, int index, String newText) {
        // DO NOTHING
    }

    public void setTaskDone(UUID todoListID, int index, boolean isDone) {
        // DO NOTHING
    }
}
