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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalCachedDatabase implements Database {

    private final FileStorageDatabase firebaseDatabase;
    private final FileStorageDatabase localDatabase;

    private TodoListCollection localTodoID;
    private Map<UUID, TodoList> inMemoryTodoMap;
    private Map<UUID, Note> inMemoryNoteMap;

    public LocalCachedDatabase(Context context) {
        this.firebaseDatabase=new FileStorageDatabase(new FirebaseFileStorageService(
                FirebaseStorage.getInstance(), UserFactory.get()));
        this.localDatabase=new FileStorageDatabase(new LocalFileStorageService(context.getCacheDir(), UserFactory.get()));

        this.localTodoID = new TodoListCollection();
        this.inMemoryNoteMap = new HashMap<>();
        this.inMemoryTodoMap = new HashMap<>();
    }

    @Override
    public CompletableFuture<TodoListCollection> getTodoListCollection() {

        CompletableFuture<TodoListCollection> localFuture = localDatabase.getTodoListCollection();


        if(localTodoID.isEmpty()) {
            CompletableFuture<TodoListCollection> firebaseFuture = firebaseDatabase.getTodoListCollection();

            firebaseFuture.thenAccept(todoListCollection -> {
                this.localTodoID = todoListCollection;
                for(int i = 0; i < todoListCollection.getSize(); ++i) {
                    this.firebaseDatabase.getTodoList(todoListCollection.getUUID(i))
                            .thenAccept(this.localDatabase::putTodoList);
                }
            });

            return firebaseFuture;
        }
    }

    @Override
    public CompletableFuture<TodoList> putTodoList(TodoList list) {
        /**
        this.firebaseDatabase.putTodoList(list);

        this.localTodoID.addUUID(list.getId());
        return this.localDatabase.putTodoList(list);
         **/
        this.firebaseDatabase.putTodoList(list);

        return CompletableFuture.completedFuture(this.inMemoryTodoMap.put(list.getId(), list));
    }

    @Override
    public CompletableFuture<Void> removeTodoList(UUID todoListID) {
        /**
        this.firebaseDatabase.removeTodoList(todoListID);

        this.localTodoID.removeUUID(todoListID);
        return this.localDatabase.removeTodoList(todoListID);
         **/
        this.firebaseDatabase.removeTodoList(todoListID);
        this.inMemoryTodoMap.remove(todoListID);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<TodoList> getTodoList(UUID todoListID) {
        /**
        if(localTodoID.contains(todoListID)) {
            return this.localDatabase.getTodoList(todoListID);
        } else {
            CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.getTodoList(todoListID);

            firebaseFuture.thenAccept(todoList -> {
                this.localTodoID.addUUID(todoListID);
                this.localDatabase.putTodoList(todoList);
            });
            return firebaseFuture;
        }
         **/
        if(inMemoryTodoMap.containsKey(todoListID)) {
            return CompletableFuture.completedFuture(inMemoryTodoMap.get(todoListID));
        } else {
            CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.getTodoList(todoListID);

            firebaseFuture.thenAccept(todo -> {
                this.inMemoryTodoMap.put(todo.getId(), todo);
            });
            return firebaseFuture;
        }
    }

    @Override
    public CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList) {
        /**
        if(localTodoID.contains(todoListID)) {
            return this.localDatabase.updateTodoList(todoListID, todoList);
        } else {
            CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.updateTodoList(todoListID, todoList);

            firebaseFuture.thenAccept(todo -> {
                this.localTodoID.addUUID(todoListID);
                this.localDatabase.putTodoList(todoList);
            });
            return firebaseFuture;
        }
         **/
        if(inMemoryTodoMap.containsKey(todoListID)) {
            this.firebaseDatabase.updateTodoList(todoListID, todoList);

            // Replace the old value by the new one:
            inMemoryTodoMap.put(todoListID, todoList);
            return CompletableFuture.completedFuture(todoList);
        } else {
            CompletableFuture<TodoList> firebaseFuture = firebaseDatabase.updateTodoList(todoListID, todoList);

            firebaseFuture.thenAccept(todo -> {
                this.inMemoryTodoMap.put(todoListID, todo);
            });
            return firebaseFuture;
        }
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
        if(inMemoryNoteMap.containsKey(noteID)) {
            return CompletableFuture.completedFuture(inMemoryNoteMap.get(noteID));
        } else {
            CompletableFuture<Note> firebaseFuture = firebaseDatabase.getNote(noteID);

            firebaseFuture.thenAccept(note -> {
                this.inMemoryNoteMap.put(noteID, note);
            });
            return firebaseFuture;
        }
    }

    @Override
    public CompletableFuture<Note> putNote(UUID noteID, Note note) {
        this.firebaseDatabase.putNote(noteID, note);

        return CompletableFuture.completedFuture(this.inMemoryNoteMap.put(noteID, note));
    }

    @Override
    public CompletableFuture<List<UUID>> getNotesList() {
        System.err.println(inMemoryNoteMap.toString() + " ============> " + inMemoryNoteMap.size());
        if(this.inMemoryNoteMap.isEmpty()) {
            CompletableFuture<List<UUID>> firebaseFuture = firebaseDatabase.getNotesList();

            firebaseFuture.thenAccept(notesList -> {
                for(UUID noteID: notesList) {
                    this.firebaseDatabase.getNote(noteID)
                            .thenAccept(note -> {
                                this.inMemoryNoteMap.put(noteID, note);
                            });
                }
            });

            return firebaseFuture;
        } else {
            return CompletableFuture.completedFuture(new ArrayList<UUID>(inMemoryNoteMap.keySet()));
        }
    }

    @Override
    public CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone) {
        return localDatabase.setTaskDone(todoListID, index, isDone);
    }
}
