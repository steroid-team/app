package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.util.JSONSerializer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FirebaseDatabase implements Database {
    private static final String TODO_LIST_PATH = "/todo-lists/";
    private static final String NOTES_PATH = "/notes/";
    private final FirebaseFileStorageService storageService;

    public FirebaseDatabase(@NonNull FirebaseFileStorageService storageService) {
        Objects.requireNonNull(storageService);

        this.storageService = storageService;
    }

    @Override
    public CompletableFuture<TodoListCollection> getTodoListCollection() {
        CompletableFuture<String[]> listDirFuture = storageService.listDir(TODO_LIST_PATH);

        return listDirFuture.thenApply(
                (fileNames) ->
                        new TodoListCollection(
                                Arrays.stream(fileNames)
                                        .map((filename) -> filename.split(".json")[0])
                                        .map(UUID::fromString)
                                        .collect(Collectors.toList())));
    }

    @Override
    public CompletableFuture<TodoList> putTodoList(@NonNull TodoList list) {
        Objects.requireNonNull(list);
        String targetPath = TODO_LIST_PATH + list.getId().toString() + ".json";

        // Serialize the task as an UTF-8 encoded JSON object.
        byte[] fileBytes = JSONSerializer.serializeTodoList(list).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fileBytes, targetPath).thenApply(str -> list);
    }

    @Override
    public CompletableFuture<Void> removeTodoList(@NonNull UUID todoListID) {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        return this.storageService
                .delete(targetPath)
                .thenCompose(
                        str -> {
                            CompletableFuture<Void> future = new CompletableFuture<>();
                            future.complete(null);
                            return future;
                        });
    }

    @Override
    public CompletableFuture<TodoList> getTodoList(@NonNull UUID todoListID) {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        return this.storageService
                .download(targetPath)
                .thenApply(
                        bytes ->
                                JSONSerializer.deserializeTodoList(
                                        new String(bytes, StandardCharsets.UTF_8)));
    }

    @Override
    public CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(todoList);

        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> todoList);
    }

    @Override
    public CompletableFuture<Task> putTask(@NonNull UUID todoListID, @NonNull Task task) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(task);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Add the new task to the object.
                .thenApply(
                        todoList -> {
                            todoList.addTask(task);
                            return todoList;
                        })
                // Re-serialize and upload the new object.
                .thenApply(JSONSerializer::serializeTodoList)
                .thenApply(
                        serializedTodoList -> serializedTodoList.getBytes(StandardCharsets.UTF_8))
                .thenCompose(bytes -> this.storageService.upload(bytes, listPath))
                .thenApply(str -> task);
    }

    @Override
    public CompletableFuture<TodoList> removeTask(
            @NonNull UUID todoListID, @NonNull Integer taskIndex) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            todoList.removeTask(taskIndex);
                            return todoList;
                        })
                .thenCompose(
                        todoList -> {
                            byte[] bytes =
                                    JSONSerializer.serializeTodoList(todoList)
                                            .getBytes(StandardCharsets.UTF_8);
                            return this.storageService
                                    .upload(bytes, listPath)
                                    .thenApply(str -> todoList);
                        });
    }

    @Override
    public CompletableFuture<Task> updateTask(UUID todoListID, Integer taskIndex, Task newTask) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            todoList.updateTask(taskIndex, newTask);
                            return todoList;
                        })
                // Re-serialize and upload the new object.
                .thenCompose(todoList -> uploadTask(todoList, taskIndex, listPath));
    }

    @Override
    public CompletableFuture<Task> getTask(@NonNull UUID todoListID, @NonNull Integer taskIndex) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID).thenApply(todoList -> todoList.getTask(taskIndex));
    }

    @Override
    public CompletableFuture<Note> getNote(UUID noteID) {
        Objects.requireNonNull(noteID);
        String notePath = NOTES_PATH + noteID.toString() + ".json";

        return this.storageService
                .download(notePath)
                .thenApply(serializedNote -> new String(serializedNote, StandardCharsets.UTF_8))
                .thenApply(JSONSerializer::deserializeNote);
    }

    @Override
    public CompletableFuture<Note> putNote(UUID noteID, Note note) {
        Objects.requireNonNull(note);
        String notePath = NOTES_PATH + noteID.toString() + ".json";
        byte[] serializedNote = JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(serializedNote, notePath).thenApply(str -> note);
    }

    @Override
    public CompletableFuture<Void> removeNote(@NonNull UUID noteID) {
        Objects.requireNonNull(noteID);
        String targetPath = NOTES_PATH + noteID.toString() + ".json";

        return this.storageService
                .delete(targetPath)
                .thenCompose(
                        str -> {
                            CompletableFuture<Void> future = new CompletableFuture<>();
                            future.complete(null);
                            return future;
                        });
    }

    @Override
    public CompletableFuture<Note> updateNote(UUID noteID, Note newNote) {
        Objects.requireNonNull(noteID);
        Objects.requireNonNull(newNote);

        String targetPath = NOTES_PATH + noteID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeNote(newNote).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> newNote);
    }

    @Override
    public CompletableFuture<List<UUID>> getNotesList() {
        CompletableFuture<String[]> listDir = this.storageService.listDir(NOTES_PATH);

        return listDir.thenApply(
                fileNames ->
                        Arrays.stream(fileNames)
                                .map(fileName -> fileName.split(".json")[0])
                                .map(UUID::fromString)
                                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone) {
        Objects.requireNonNull(todoListID);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            Task task = todoList.getTask(index);
                            task.setDone(isDone);
                            return todoList;
                        })
                // Re-serialize and upload the new object.
                .thenCompose(todoList -> uploadTask(todoList, index, listPath));
    }

    @Override
    public CompletableFuture<Long> getLastModifiedTimeTodo(@NonNull UUID todoListID) {
        return null;
    }

    @Override
    public CompletableFuture<Long> getLastModifiedTimeNote(@NonNull UUID noteID) {
        return null;
    }

    private CompletableFuture<Task> uploadTask(TodoList todoList, int index, String path) {
        Task updatedTask = todoList.getTask(index);
        byte[] bytes = JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);
        return this.storageService.upload(bytes, path).thenApply(str -> updatedTask);
    }
}
