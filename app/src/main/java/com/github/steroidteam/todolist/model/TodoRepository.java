package com.github.steroidteam.todolist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TodoRepository {

    private VolatileDatabase database;
    private MutableLiveData<TodoList> oneTodoList;

    // ====== TO DELETE ======== BEGIN
    // We don't have persistent database !
    // So we set a public attribute to gets the ID
    // of the todoList in the item view model.
    public UUID id;
    // ========================= END

    public TodoRepository() {
        this.database = new VolatileDatabase();

        // ====== TO DELETE ======== BEGIN
        // We don't have persistent database !
        // We need to add a todoList,
        // Otherwise we will get a NullPointerException
        // in the getTodoList method.
        TodoList tl = new TodoList("A Todo!");
        this.database.putTodoList(tl);
        id = tl.getId();
        // ========================= END
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        if (oneTodoList == null) {
            try {
                oneTodoList = new MutableLiveData<TodoList>(this.database.getTodoList(todoListID).get());
            } catch (ExecutionException | InterruptedException e) {
                return new MutableLiveData<>();
            }
        }
        return this.oneTodoList;
    }

    public void putTask(UUID todoListID, Task task) {
        this.database.putTask(todoListID, task);
        try {
            this.oneTodoList.setValue(this.database.getTodoList(todoListID).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeTask(UUID todoListID, int index) {
        this.database.removeTask(todoListID, index);
        try {
            this.oneTodoList.setValue(this.database.getTodoList(todoListID).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }    }

    public void renameTask(UUID todoListID, int index, String newText) {
        this.database.renameTask(todoListID, index, newText);
        try {
            this.oneTodoList.setValue(this.database.getTodoList(todoListID).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }    }
}
