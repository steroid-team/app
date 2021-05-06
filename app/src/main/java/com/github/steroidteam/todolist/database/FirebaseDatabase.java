package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.todo.TodoListCollection;
import com.github.steroidteam.todolist.util.JSONSerializer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FirebaseDatabase implements Database {
    private static final String TODO_LIST_PATH = "/todo-lists/";
    private static final String NOTES_PATH = "/notes/";
    private static final String AUDIO_MEMOS_PATH = "/audio-memos/";
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
                .downloadBytes(targetPath)
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
                .downloadBytes(notePath)
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
    public CompletableFuture<Void> setAudioMemo(UUID noteID, String audioMemoPath)
            throws FileNotFoundException
    {
        Objects.requireNonNull(noteID);
        Objects.requireNonNull(audioMemoPath);

        UUID audioMemoID = UUID.randomUUID();
        String remoteAudioMemoPath = AUDIO_MEMOS_PATH + audioMemoID;

        InputStream is = new FileInputStream(new File(audioMemoPath));

        /* First, upload the audio memo */
        CompletableFuture<String> audioUploadFuture = this.storageService.upload(is, remoteAudioMemoPath);

        /* In the mean time, get the Note then set the associated audio memo ID,
        * then synchronize everything */
        return getNote(noteID).thenCompose(note -> {
            note.setAudioMemoId(audioMemoID);
            return uploadNote(note);
        }).thenCompose(note -> audioUploadFuture).thenApply(str -> null);
    }

    @Override
    public CompletableFuture<Void> removeAudioMemo(UUID noteID) {
        return getNote(noteID).thenCompose(note -> {
            Optional<UUID> audioMemoID = note.getAudioMemoId();

            /* If there is some audio memo to remove */
            if (audioMemoID.isPresent()) {
                note.removeAudioMemoId();
                CompletableFuture<Note> uploadNoteFuture = uploadNote(note);
                return this.storageService.delete(AUDIO_MEMOS_PATH + audioMemoID.get())
                        .thenCompose(str -> uploadNoteFuture)
                        .thenApply(updatedNote -> null);
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    public CompletableFuture<File> getAudioMemo(
            @NonNull UUID audioID, @NonNull String destinationPath)
    {
        String audioFilePath = AUDIO_MEMOS_PATH + audioID.toString();

        return this.storageService.downloadFile(audioFilePath, destinationPath);
    }

    private CompletableFuture<Task> uploadTask(TodoList todoList, int index, String path) {
        Task updatedTask = todoList.getTask(index);
        byte[] bytes = JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);
        return this.storageService.upload(bytes, path).thenApply(str -> updatedTask);
    }

    private CompletableFuture<Note> uploadNote(Note note) {
        String notePath = NOTES_PATH + note.getId().toString() + ".json";

        byte[] bytes = JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);
        return this.storageService.upload(bytes, notePath).thenApply(str -> note);
    }
}
