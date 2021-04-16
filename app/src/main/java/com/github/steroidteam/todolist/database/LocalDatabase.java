package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.util.JSONSerializer;
import java.util.Objects;
import java.util.UUID;

public class LocalDatabase implements Database {

    private final LocalFileStorageService storageService;

    public LocalDatabase(@NonNull LocalFileStorageService storageService) {
        Objects.requireNonNull(storageService);

        this.storageService = storageService;
    }

    @Override
    public void putTodoList(TodoList list) throws DatabaseException {
        Objects.requireNonNull(list);
        String targetPath = TODO_LIST_PATH + list.getId().toString() + ".json";

        String listSerialized = JSONSerializer.serializeTodoList(list);

        this.storageService.upload(listSerialized, targetPath);
    }

    @Override
    public void removeTodoList(UUID todoListID) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        this.storageService.delete(targetPath);
    }

    @Override
    public TodoList getTodoList(UUID todoListID) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        String todoString = storageService.download(targetPath);

        return JSONSerializer.deserializeTodoList(todoString);
    }

    @Override
    public void putTask(UUID todoListID, Task task) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(task);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        String todoString = storageService.download(targetPath);
        TodoList todo = JSONSerializer.deserializeTodoList(todoString);
        todo.addTask(task);

        putTodoList(todo);
    }

    @Override
    public void removeTask(UUID todoListID, Integer taskIndex) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        String todoString = storageService.download(targetPath);
        TodoList todo = JSONSerializer.deserializeTodoList(todoString);
        todo.removeTask(taskIndex);

        putTodoList(todo);
    }

    @Override
    public Task getTask(UUID todoListID, Integer taskIndex) throws DatabaseException {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(taskIndex);
        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        String todoString = storageService.download(targetPath);

        return JSONSerializer.deserializeTodoList(todoString).getTask(taskIndex);
    }
}
