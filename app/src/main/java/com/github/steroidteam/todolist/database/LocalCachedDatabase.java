package com.github.steroidteam.todolist.database;

import android.content.Context;

import com.github.steroidteam.todolist.filestorage.FileStorageService;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalCachedDatabase implements Database {

    private final FileStorageDatabase firebaseDatabase;
    private final FileStorageDatabase localDatabase;

    public LocalCachedDatabase(Context context) {
        this.firebaseDatabase=new FileStorageDatabase(new FirebaseFileStorageService(
                FirebaseStorage.getInstance(), UserFactory.get()));
        this.localDatabase=new FileStorageDatabase(new LocalFileStorageService(context.getCacheDir(), UserFactory.get()));
    }

    @Override
    public CompletableFuture<TodoListCollection> getTodoListCollection() {

        CompletableFuture<TodoListCollection> firebaseFuture = firebaseDatabase.getTodoListCollection();
        CompletableFuture<TodoListCollection> localFuture = localDatabase.getTodoListCollection();
        return localFuture;
    }

    @Override
    public CompletableFuture<TodoList> putTodoList(TodoList list) {
        return firebaseDatabase.putTodoList(list);
    }

    @Override
    public CompletableFuture<Void> removeTodoList(UUID todoListID) {
        return localDatabase.removeTodoList(todoListID);
    }

    @Override
    public CompletableFuture<TodoList> getTodoList(UUID todoListID) {
        return localDatabase.getTodoList(todoListID);
    }

    @Override
    public CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList) {
        return localDatabase.updateTodoList(todoListID, todoList);
    }

    @Override
    public CompletableFuture<Task> putTask(UUID todoListID, Task task) {
        return localDatabase.putTask(todoListID, task);
    }

    @Override
    public CompletableFuture<TodoList> removeTask(UUID todoListID, Integer taskIndex) {
        return localDatabase.removeTask(todoListID, taskIndex);
    }

    @Override
    public CompletableFuture<Task> renameTask(UUID todoListID, Integer taskIndex, String newName) {
        return localDatabase.renameTask(todoListID, taskIndex, newName);
    }

    @Override
    public CompletableFuture<Task> getTask(UUID todoListID, Integer taskIndex) {
        return localDatabase.getTask(todoListID, taskIndex);
    }

    @Override
    public CompletableFuture<Note> getNote(UUID noteID) {
        return localDatabase.getNote(noteID);
    }

    @Override
    public CompletableFuture<Note> putNote(UUID noteID, Note note) {
        return localDatabase.putNote(noteID, note);
    }

    @Override
    public CompletableFuture<List<UUID>> getNotesList() {
        return localDatabase.getNotesList();
    }

    @Override
    public CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone) {
        return localDatabase.setTaskDone(todoListID, index, isDone);
    }
}
