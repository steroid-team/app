package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.util.JSONSerializer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FirebaseDatabase implements Database {
    private final FirebaseFileStorageService storageService;

    // TODO: Run Database operations asynchronously.
    public FirebaseDatabase(@NonNull FirebaseFileStorageService storageService) {
        Objects.requireNonNull(storageService);

        this.storageService = storageService;
    }

    @Override
    public void putTodoList(@NonNull TodoList list) throws DatabaseException {
        Objects.requireNonNull(list);
        String targetPath = TODO_LIST_PATH + list.getId().toString() + ".json";

        // Serialize the task as an UTF-8 encoded JSON object.
        byte[] fileBytes = JSONSerializer.serializeTodoList(list).getBytes(StandardCharsets.UTF_8);

        try {
            this.storageService.upload(fileBytes, targetPath).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    @Override
    public void removeTodoList(@NonNull UUID todoListID) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        try {
            this.storageService.delete(targetPath).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    @Override
    public TodoList getTodoList(@NonNull UUID todoListID) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        try {
            String serializedList =
                    new String(
                            this.storageService.download(targetPath).get(), StandardCharsets.UTF_8);
            return JSONSerializer.deserializeTodoList(serializedList);
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    @Override
    public void putTask(@NonNull UUID todoListID, @NonNull Task task) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(task);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        try {
            this.storageService
                    .download(listPath)
                    // Deserialize it.
                    .thenApply(serializedList -> new String(serializedList, StandardCharsets.UTF_8))
                    .thenApply(JSONSerializer::deserializeTodoList)
                    // Add the new task to the object.
                    .thenApply(
                            todoList -> {
                                todoList.addTask(task);
                                return todoList;
                            })
                    // Re-serialize and upload the new object.
                    .thenApply(JSONSerializer::serializeTodoList)
                    .thenApply(
                            serializedTodoList ->
                                    serializedTodoList.getBytes(StandardCharsets.UTF_8))
                    .thenApply(bytes -> this.storageService.upload(bytes, listPath))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    @Override
    public void removeTask(@NonNull UUID todoListID, @NonNull Integer taskIndex)
            throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        try {
            this.storageService
                    .download(listPath)
                    // Deserialize it.
                    .thenApply(serializedList -> new String(serializedList, StandardCharsets.UTF_8))
                    .thenApply(JSONSerializer::deserializeTodoList)
                    // Remove the task from the object.
                    .thenApply(
                            todoList -> {
                                todoList.removeTask(taskIndex);
                                return todoList;
                            })
                    // Re-serialize and upload the new object.
                    .thenApply(JSONSerializer::serializeTodoList)
                    .thenApply(
                            serializedTodoList ->
                                    serializedTodoList.getBytes(StandardCharsets.UTF_8))
                    .thenApply(bytes -> this.storageService.upload(bytes, listPath))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }

    @Override
    public Task getTask(@NonNull UUID todoListID, @NonNull Integer taskIndex)
            throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        try {
            return this.storageService
                    .download(listPath)
                    // Deserialize it.
                    .thenApply(serializedList -> new String(serializedList, StandardCharsets.UTF_8))
                    .thenApply(JSONSerializer::deserializeTodoList)
                    .thenApply(todoList -> todoList.getTask(taskIndex))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseException(e.toString());
        }
    }
}
