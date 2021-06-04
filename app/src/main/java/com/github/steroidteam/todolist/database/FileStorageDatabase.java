package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;

import com.github.steroidteam.todolist.filestorage.FileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.util.JSONSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.steroidteam.todolist.util.Utils.checkNonNullArgs;

public class FileStorageDatabase implements Database {
    private static final String TODO_LIST_PATH = "/todo-lists/";
    private static final String NOTES_PATH = "/notes/";
    private static final String AUDIO_MEMOS_PATH = "/audio-memos/";
    private static final String TAGS_PATH = "/tags/";
    private static final String IMAGES_PATH = "/images/";
    private final FileStorageService storageService;

    public FileStorageDatabase(@NonNull FileStorageService storageService) {
        checkNonNullArgs(storageService);

        this.storageService = storageService;
    }

    @Override
    public CompletableFuture<Long> getLastModifiedTimeTodo(@NonNull UUID todoListID) {
        checkNonNullArgs(todoListID);

        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";
        return storageService.getLastModifiedTime(targetPath);
    }

    @Override
    public CompletableFuture<Long> getLastModifiedTimeNote(@NonNull UUID noteID) {
        checkNonNullArgs(noteID);

        String targetPath = NOTES_PATH + noteID.toString() + ".json";
        return storageService.getLastModifiedTime(targetPath);
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
        checkNonNullArgs(list);

        String targetPath = TODO_LIST_PATH + list.getId().toString() + ".json";

        // Serialize the task as an UTF-8 encoded JSON object.
        byte[] fileBytes = JSONSerializer.serializeTodoList(list).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fileBytes, targetPath).thenApply(str -> list);
    }

    @Override
    public CompletableFuture<Void> removeTodoList(@NonNull UUID todoListID) {
        checkNonNullArgs(todoListID);
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
        checkNonNullArgs(todoListID);
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
        checkNonNullArgs(todoListID, todoList);

        String targetPath = TODO_LIST_PATH + todoListID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> todoList);
    }

    @Override
    public CompletableFuture<Task> putTask(@NonNull UUID todoListID, @NonNull Task task) {
        checkNonNullArgs(todoListID, task);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Add the new task to the object.
                .thenApply(
                        todoList -> {
                            todoList.addTask(task);
                            return todoList;
                        })
                .thenApply(todoList -> todoList.sortByDate())
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
        checkNonNullArgs(todoListID, taskIndex);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            todoList.removeTask(taskIndex);
                            return todoList;
                        })
                .thenApply(todoList -> todoList.sortByDate())
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
    public CompletableFuture<TodoList> removeDoneTasks(@NonNull UUID todoListID) {
        Objects.requireNonNull(todoListID);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update
        return getTodoList(todoListID)
                // Remove all done tasks from the object
                .thenApply(
                        todoList -> {
                            todoList.removeDoneTasks();
                            return todoList;
                        })
                .thenApply(todoList -> todoList.sortByDate())
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
        checkNonNullArgs(todoListID, taskIndex, newTask);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the task from the object.
                .thenApply(
                        todoList -> {
                            todoList.updateTask(taskIndex, newTask);
                            return todoList;
                        })
                .thenApply(todoList -> todoList.sortByDate())
                // Re-serialize and upload the new object.
                .thenCompose(todoList -> uploadTask(todoList, taskIndex, listPath));
    }

    @Override
    public CompletableFuture<Task> getTask(@NonNull UUID todoListID, @NonNull Integer taskIndex) {
        checkNonNullArgs(todoListID, taskIndex);

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID).thenApply(todoList -> todoList.getTask(taskIndex));
    }

    @Override
    public CompletableFuture<Note> getNote(UUID noteID) {
        checkNonNullArgs(noteID);
        String notePath = NOTES_PATH + noteID.toString() + ".json";

        return this.storageService
                .downloadBytes(notePath)
                .thenApply(serializedNote -> new String(serializedNote, StandardCharsets.UTF_8))
                .thenApply(JSONSerializer::deserializeNote);
    }

    @Override
    public CompletableFuture<Note> putNote(UUID noteID, Note note) {
        checkNonNullArgs(note);
        String notePath = NOTES_PATH + noteID.toString() + ".json";
        byte[] serializedNote = JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(serializedNote, notePath).thenApply(str -> note);
    }

    @Override
    public CompletableFuture<Void> removeNote(UUID noteID) {
        checkNonNullArgs(noteID);
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
    public CompletableFuture<List<UUID>> getNotesList() {
        return getListFromPath(NOTES_PATH);
    }

    @Override
    public CompletableFuture<Note> updateNote(UUID noteID, Note newNote) {
        checkNonNullArgs(noteID, newNote);

        String targetPath = NOTES_PATH + noteID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeNote(newNote).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> newNote);
    }

    @Override
    public CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone) {
        checkNonNullArgs(todoListID);
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
            throws FileNotFoundException {
        Objects.requireNonNull(noteID);
        Objects.requireNonNull(audioMemoPath);

        UUID audioMemoID = UUID.randomUUID();
        String remoteAudioMemoPath = AUDIO_MEMOS_PATH + audioMemoID;

        InputStream is = new FileInputStream(new File(audioMemoPath));

        /* First, upload the audio memo */
        CompletableFuture<String> audioUploadFuture =
                this.storageService.upload(is, remoteAudioMemoPath);

        /* In the mean time, get the Note then set the associated audio memo ID,
         * then synchronize everything */
        return getNote(noteID)
                .thenCompose(
                        note -> {
                            note.setAudioMemoId(audioMemoID);
                            return uploadNote(note);
                        })
                .thenCompose(note -> audioUploadFuture)
                .thenApply(str -> null);
    }

    @Override
    public CompletableFuture<Void> removeAudioMemo(UUID noteID) {
        return getNote(noteID)
                .thenCompose(
                        note -> {
                            Optional<UUID> audioMemoID = note.getAudioMemoId();

                            /* If there is some audio memo to remove */
                            if (audioMemoID.isPresent()) {
                                note.removeAudioMemoId();
                                CompletableFuture<Note> uploadNoteFuture = uploadNote(note);
                                return this.storageService
                                        .delete(AUDIO_MEMOS_PATH + audioMemoID.get())
                                        .thenCompose(str -> uploadNoteFuture)
                                        .thenApply(updatedNote -> null);
                            } else {
                                return CompletableFuture.completedFuture(null);
                            }
                        });
    }

    @Override
    public CompletableFuture<File> getAudioMemo(
            @NonNull UUID audioID, @NonNull String destinationPath) {
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

    public CompletableFuture<Tag> putTag(@NonNull Tag tag) {
        Objects.requireNonNull(tag);
        String targetPath = TAGS_PATH + tag.getId().toString() + ".json";

        // Serialize the task as an UTF-8 encoded JSON object.
        byte[] fileBytes = JSONSerializer.serializeTag(tag).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fileBytes, targetPath).thenApply(str -> tag);
    }

    private void removeTagFromTDLs(UUID tagId) {
        getTodoListCollection()
                .thenAccept(
                        col -> {
                            for (int i = 0; i < col.getSize(); ++i) {
                                UUID todoListId = col.getUUID(i);
                                getTodoList(todoListId)
                                        .thenAccept(
                                                todoList -> {
                                                    if (todoList.containsTag(tagId)) {
                                                        removeTagFromList(todoListId, tagId);
                                                    }
                                                });
                            }
                        });
    }

    public CompletableFuture<Void> removeTag(UUID tagId) {
        Objects.requireNonNull(tagId);
        String targetPath = TAGS_PATH + tagId.toString() + ".json";

        removeTagFromTDLs(tagId);

        return this.storageService
                .delete(targetPath)
                .thenCompose(
                        str -> {
                            CompletableFuture<Void> future = new CompletableFuture<>();
                            future.complete(null);
                            return future;
                        });
    }

    public CompletableFuture<Tag> getTag(UUID tagID) {
        Objects.requireNonNull(tagID);
        String targetPath = TAGS_PATH + tagID.toString() + ".json";

        return this.storageService
                .downloadBytes(targetPath)
                .thenApply(
                        bytes ->
                                JSONSerializer.deserializeTag(
                                        new String(bytes, StandardCharsets.UTF_8)));
    }

    public CompletableFuture<Tag> updateTag(UUID tagID, Tag tag) {
        Objects.requireNonNull(tagID);
        Objects.requireNonNull(tag);

        String targetPath = TAGS_PATH + tagID.toString() + ".json";
        byte[] fBytes = JSONSerializer.serializeTag(tag).getBytes(StandardCharsets.UTF_8);

        return this.storageService.upload(fBytes, targetPath).thenApply(str -> tag);
    }

    public CompletableFuture<UUID> putTagInList(UUID todoListID, UUID tagId) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(tagId);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Add the new task to the object.
                .thenApply(
                        todoList -> {
                            todoList.addTagId(tagId);
                            return todoList;
                        })
                // Re-serialize and upload the new object.
                .thenApply(JSONSerializer::serializeTodoList)
                .thenApply(
                        serializedTodoList -> serializedTodoList.getBytes(StandardCharsets.UTF_8))
                .thenCompose(bytes -> this.storageService.upload(bytes, listPath))
                .thenApply(str -> tagId);
    }

    public CompletableFuture<TodoList> removeTagFromList(UUID todoListID, UUID tagId) {
        Objects.requireNonNull(todoListID);
        Objects.requireNonNull(tagId);
        String listPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Fetch the remote list that we are about to update.
        return getTodoList(todoListID)
                // Remove the tag from the object.
                .thenApply(
                        todoList -> {
                            todoList.removeTagId(tagId);
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

    public CompletableFuture<List<UUID>> getAllTagsIds() {
        return getListFromPath(TAGS_PATH);
    }

    public CompletableFuture<List<Tag>> getAllTags() {
        return getAllTagsIds().thenCompose(ids -> getTagsFromIds(ids));
    }

    public CompletableFuture<List<Tag>> getTagsFromIds(List<UUID> ids) {
        List<CompletableFuture<Tag>> tagFutures =
                ids.stream().map(this::getTag).collect(Collectors.toList());

        CompletableFuture<Void> futureOfList =
                CompletableFuture.allOf(tagFutures.toArray(new CompletableFuture[0]));

        return futureOfList.thenApply(
                v ->
                        tagFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.<Tag>toList()));
    }

    public CompletableFuture<List<UUID>> getTagsIdsFromList(@NonNull UUID todoListID) {
        Objects.requireNonNull(todoListID);
        return getTodoList(todoListID).thenApply(TodoList::getTagsIds);
    }

    public CompletableFuture<List<Tag>> getTagsFromList(UUID listId) {
        return getTodoList(listId)
                .thenApply(TodoList::getTagsIds)
                .thenCompose(this::getTagsFromIds);
    }

    @Override
    public CompletableFuture<Void> setHeaderNote(UUID noteID, String imagePath, UUID imageID)
            throws FileNotFoundException {
        Objects.requireNonNull(noteID);
        Objects.requireNonNull(imagePath);

        String fileSystemHeaderPath = IMAGES_PATH + imageID + ".jpeg";

        InputStream is = new FileInputStream(new File(imagePath));

        /* First, upload the header image */
        CompletableFuture<String> headerUploadFuture =
                this.storageService.upload(is, fileSystemHeaderPath);

        /* In the mean time, get the Note then set the associated header ID,
         * then synchronize everything */

        CompletableFuture<Note> currentNote = getNote(noteID);

        // REMOVE PREVIOUS HEADER IF PRESENT
        currentNote.thenCompose(
                note -> {
                    Optional<UUID> headerID = note.getHeaderID();
                    if (headerID.isPresent()) {
                        return this.storageService
                                .delete(IMAGES_PATH + headerID.get() + ".jpeg")
                                .thenApply(updatedNote -> null);
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                });

        // STORE NEW HEADER
        return getNote(noteID)
                .thenCompose(
                        note -> {
                            note.setHeader(imageID);
                            return uploadNote(note);
                        })
                .thenCompose(note -> headerUploadFuture)
                .thenApply(str -> null);
    }

    @Override
    public CompletableFuture<Void> removeImage(UUID imageID) {
        return this.storageService.delete(IMAGES_PATH + imageID + ".jpeg").thenApply(v -> null);
    }

    @Override
    public CompletableFuture<File> getImage(
            @NonNull UUID imageID, @NonNull String destinationPath) {
        String imageFilePath = IMAGES_PATH + imageID + ".jpeg";
        return this.storageService.downloadFile(imageFilePath, destinationPath);
    }

    private CompletableFuture<List<UUID>> getListFromPath(String path) {
        CompletableFuture<String[]> listDir = this.storageService.listDir(path);

        return listDir.thenApply(
                fileNames ->
                        Arrays.stream(fileNames)
                                .map(fileName -> fileName.split(".json")[0])
                                .map(UUID::fromString)
                                .collect(Collectors.toList()));
    }
}
