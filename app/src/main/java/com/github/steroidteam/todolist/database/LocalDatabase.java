package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class LocalDatabase {

    private final LocalFileStorageService storageService;
    private static final String TODO_LIST_PATH = "/todo-lists";
    private static final String NOTES_PATH = "/notes/";

    public LocalDatabase(@NonNull LocalFileStorageService storageService) {
        Objects.requireNonNull(storageService);

        this.storageService = storageService;
    }

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

    public CompletableFuture<TodoList> putTodoList(TodoList list) {
        Objects.requireNonNull(list);
        String targetPath = TODO_LIST_PATH + list.getId().toString() + ".json";

        // Serialize the task as an UTF-8 encoded JSON object.
        byte[] listSerialized =
                JSONSerializer.serializeTodoList(list).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(listSerialized, targetPath).thenApply(str -> list);
    }

    public void removeTodoList(UUID todoListID) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        try {
            this.storageService.delete(targetPath).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    public CompletableFuture<TodoList> getTodoList(UUID todoListID) {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        return this.storageService
                .download(targetPath)
                .thenApply(
                        bytes ->
                                JSONSerializer.deserializeTodoList(
                                        new String(bytes, StandardCharsets.UTF_8)));
    }

    public CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(todoList);

        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> todoList);
    }

    public CompletableFuture<Task> putTask(UUID todoListID, Task task) throws DatabaseException {
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

    public CompletableFuture<Task> renameTask(UUID todoListID, Integer taskIndex, String newName) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            todoList.renameTask(taskIndex, newName);
                            return todoList;
                        })
                // Re-serialize and upload the new object.
                .thenCompose(
                        todoList -> {
                            Task renamedTask = todoList.getTask(taskIndex);
                            byte[] bytes =
                                    JSONSerializer.serializeTodoList(todoList)
                                            .getBytes(StandardCharsets.UTF_8);
                            return this.storageService
                                    .upload(bytes, listPath)
                                    .thenApply(str -> renamedTask);
                        });
    }

    public CompletableFuture<Task> getTask(@NonNull UUID todoListID, @NonNull Integer taskIndex) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID).thenApply(todoList -> todoList.getTask(taskIndex));
    }

    public CompletableFuture<Note> getNote(UUID noteID) {
        Objects.requireNonNull(noteID);
        String notePath = NOTES_PATH + noteID.toString() + ".json";

        return this.storageService
                .download(notePath)
                .thenApply(serializedNote -> new String(serializedNote, StandardCharsets.UTF_8))
                .thenApply(JSONSerializer::deserializeNote);
    }

    public CompletableFuture<Note> putNote(UUID noteID, Note note) {
        Objects.requireNonNull(note);
        String notePath = NOTES_PATH + noteID.toString() + ".json";
        byte[] serializedNote = JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(serializedNote, notePath).thenApply(str -> note);
    }

    public CompletableFuture<List<UUID>> getNotesList() {
        CompletableFuture<String[]> listDir = this.storageService.listDir(NOTES_PATH);

        return listDir.thenApply(
                fileNames ->
                        Arrays.stream(fileNames)
                                .map(fileName -> fileName.split(".json")[0])
                                .map(UUID::fromString)
                                .collect(Collectors.toList()));
    }

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
                .thenCompose(
                        todoList -> {
                            Task updatedTask = todoList.getTask(index);
                            byte[] bytes =
                                    JSONSerializer.serializeTodoList(todoList)
                                            .getBytes(StandardCharsets.UTF_8);
                            return this.storageService
                                    .upload(bytes, listPath)
                                    .thenApply(str -> updatedTask);
                        });
    }
}
