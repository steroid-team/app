package com.github.steroidteam.todolist.database;

import android.content.Context;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.storage.FirebaseStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalCachedDatabase {

    private final FileStorageDatabase firebaseDatabase;
    private final FileStorageDatabase localDatabase;

    private TodoListCollection localTodoID;
    private Map<UUID, TodoList> inMemoryTodoMap;
    private Map<UUID, Note> inMemoryNoteMap;

    public LocalCachedDatabase(Context context) {
        this.firebaseDatabase =
                new FileStorageDatabase(
                        new FirebaseFileStorageService(
                                FirebaseStorage.getInstance(), UserFactory.get()));
        this.localDatabase =
                new FileStorageDatabase(
                        new LocalFileStorageService(context.getCacheDir(), UserFactory.get()));

        this.localTodoID = new TodoListCollection();
        this.inMemoryNoteMap = new HashMap<>();
        this.inMemoryTodoMap = new HashMap<>();
    }

    public CompletableFuture<TodoList> putTodoList(TodoList list) {
        /**
         * this.firebaseDatabase.putTodoList(list);
         *
         * <p>this.localTodoID.addUUID(list.getId()); return this.localDatabase.putTodoList(list);
         */
        this.firebaseDatabase.putTodoList(list);

        return CompletableFuture.completedFuture(this.inMemoryTodoMap.put(list.getId(), list));
    }

    public CompletableFuture<Void> removeTodoList(UUID todoListID) {
        /**
         * this.firebaseDatabase.removeTodoList(todoListID);
         *
         * <p>this.localTodoID.removeUUID(todoListID); return
         * this.localDatabase.removeTodoList(todoListID);
         */
        this.firebaseDatabase.removeTodoList(todoListID);
        this.inMemoryTodoMap.remove(todoListID);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<TodoList> getTodoList(UUID todoListID) {
        /**
         * if(localTodoID.contains(todoListID)) { return this.localDatabase.getTodoList(todoListID);
         * } else { CompletableFuture<TodoList> firebaseFuture =
         * firebaseDatabase.getTodoList(todoListID);
         *
         * <p>firebaseFuture.thenAccept(todoList -> { this.localTodoID.addUUID(todoListID);
         * this.localDatabase.putTodoList(todoList); }); return firebaseFuture; }
         */
        if (inMemoryTodoMap.containsKey(todoListID)) {
            return CompletableFuture.completedFuture(inMemoryTodoMap.get(todoListID));
        } else {
            CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.getTodoList(todoListID);

            firebaseFuture.thenAccept(
                    todo -> {
                        this.inMemoryTodoMap.put(todo.getId(), todo);
                    });
            return firebaseFuture;
        }
    }

    public CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList) {
        /**
         * if(localTodoID.contains(todoListID)) { return
         * this.localDatabase.updateTodoList(todoListID, todoList); } else {
         * CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.updateTodoList(todoListID,
         * todoList);
         *
         * <p>firebaseFuture.thenAccept(todo -> { this.localTodoID.addUUID(todoListID);
         * this.localDatabase.putTodoList(todoList); }); return firebaseFuture; }
         */
        if (inMemoryTodoMap.containsKey(todoListID)) {
            this.firebaseDatabase.updateTodoList(todoListID, todoList);

            // Replace the old value by the new one:
            inMemoryTodoMap.put(todoListID, todoList);
            return CompletableFuture.completedFuture(todoList);
        } else {
            CompletableFuture<TodoList> firebaseFuture =
                    firebaseDatabase.updateTodoList(todoListID, todoList);

            firebaseFuture.thenAccept(
                    todo -> {
                        this.inMemoryTodoMap.put(todoListID, todo);
                    });
            return firebaseFuture;
        }
    }

    public CompletableFuture<Task> putTask(UUID todoListID, Task task) {
        return localDatabase.putTask(todoListID, task);
    }

    public CompletableFuture<TodoList> removeTask(UUID todoListID, Integer taskIndex) {
        return localDatabase.removeTask(todoListID, taskIndex);
    }
}
