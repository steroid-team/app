package com.github.steroidteam.todolist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.steroidteam.todolist.database.FirebaseDatabase;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TodoRepository {
    private final FirebaseDatabase database;
    private final MutableLiveData<TodoList> oneTodoList;
    private final UUID todoListID;

    public TodoRepository(UUID todoListID) {
        this.database = new FirebaseDatabase(new FirebaseFileStorageService(
                FirebaseStorage.getInstance(), FirebaseAuth.getInstance().getCurrentUser()));
        oneTodoList = new MutableLiveData<>();
        this.todoListID = todoListID;

        this.database.getTodoList(todoListID).thenAccept(this.oneTodoList::setValue);
    }

    public LiveData<TodoList> getTodoList() {
        return this.oneTodoList;
    }

    public void putTask(Task task) {
        this.database.putTask(todoListID, task)
                .thenCompose(str -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void removeTask(int index) {
        this.database.removeTask(todoListID, index)
                .thenCompose(str -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void renameTask(int index, String newText) {
        this.database.renameTask(todoListID, index, newText)
                .thenCompose(task -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void setTaskDone(UUID todoListID, int index, boolean isDone) {
        this.database.doneTask(todoListID, index, isDone);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }
}
