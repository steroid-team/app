package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** A database that stores pairs of key and value. */
public interface Database {

    /**
     * Returns the unique to-do list collection of the current user
     *
     * @return The unique to-do list collection.
     */
    CompletableFuture<TodoListCollection> getTodoListCollection();

    /**
     * Pushes a new to-do list in the database.
     *
     * @param list The to-do list to push.
     */
    CompletableFuture<TodoList> putTodoList(TodoList list);

    /**
     * Removes a to-do list from the database.
     *
     * @param todoListID The ID of the to-do list to remove.
     * @throws IllegalArgumentException Thrown if the argument is null.
     */
    CompletableFuture<Void> removeTodoList(UUID todoListID);

    /**
     * Gets the to-do list given the ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if the ID isn't in the database.
     */
    CompletableFuture<TodoList> getTodoList(UUID todoListID);

    /**
     * Updates the to-do list given the ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if the ID isn't in the database.
     */
    CompletableFuture<TodoList> updateTodoList(UUID todoListID, TodoList todoList);

    /**
     * Pushes a new task in the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task The new task to add.
     * @return the task put in the database
     */
    CompletableFuture<Task> putTask(UUID todoListID, Task task);

    /**
     * Removes a task from the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the list.
     * @return the updated todoList
     */
    CompletableFuture<TodoList> removeTask(UUID todoListID, Integer taskIndex);

    /**
     * Updates a task in the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the list.
     * @param newTask The new task.
     */
    CompletableFuture<Task> updateTask(UUID todoListID, Integer taskIndex, Task newTask);

    /**
     * Gets the task from the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task you want from a to-do list.
     * @return The task or null if the key does not exist in the database.
     */
    CompletableFuture<Task> getTask(UUID todoListID, Integer taskIndex);

    /**
     * Gets the note from the database
     *
     * @param noteID The id of the note
     * @return the note
     */
    CompletableFuture<Note> getNote(UUID noteID);

    /**
     * Puts a note to the database
     *
     * @param noteID The id of the note
     * @param note The note object to put
     * @return the saved note
     */
    CompletableFuture<Note> putNote(UUID noteID, Note note);

    /**
     * Removes a note from the database
     *
     * @param noteID The id of the note
     */
    CompletableFuture<Void> removeNote(UUID noteID);

    /**
     * Gets the list of note IDs
     *
     * @return The list of note ids.
     */
    CompletableFuture<List<UUID>> getNotesList();

    /**
     * Updates the note list given the ID.
     *
     * @param noteID The ID of the note.
     * @return The note or null if the ID isn't in the database.
     */
    CompletableFuture<Note> updateNote(UUID noteID, Note newNote);

    /**
     * Sets "done" flag in a task
     *
     * @param todoListID id of the todolist containing the task
     * @param index of the task inside the todolist
     * @param isDone the state of the flag to set
     * @return the updated task
     */
    CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone);

    /**
     * Return the time when the last modification in the to-do list has been made.
     *
     * @param todoListID id of the todolist
     * @return the last modified time
     */
    CompletableFuture<Long> getLastModifiedTimeTodo(UUID todoListID);

    /**
     * Return the time when the last modification in the note has been made.
     *
     * @param noteID id of the note
     * @return the last modified time
     */
    CompletableFuture<Long> getLastModifiedTimeNote(UUID noteID);

    /**
     * Saves and associate an audio memo to a note. If an audio memo already exists, it is replaced
     * and DELETED !
     *
     * @param noteID the note UUID to wich the audio memo is attached to
     * @param audioMemoPath A file path to the audio memo to upload
     * @return a void future which completes whenever the audio memo is persisted in the database.
     */
    CompletableFuture<Void> setAudioMemo(UUID noteID, String audioMemoPath)
            throws FileNotFoundException;

    /**
     * Removes and deletes the associated voice memo of a note
     *
     * @param noteID the note UUID to wich the audio memo is attached to
     * @return a void future which completes whenever the audio memo is persisted in the database.
     */
    CompletableFuture<Void> removeAudioMemo(UUID noteID);

    /**
     * Downloads an audio file and stores it at the specified destination path on the local
     * filesystem.
     *
     * @param audioID the UUID of the audio memo to download
     * @param destinationPath the path on the local filesystem where the file should be stored when
     *     download completes.
     * @return a File future which completes whenever the audio memo is present on the local
     *     filesystem.
     */
    CompletableFuture<File> getAudioMemo(UUID audioID, String destinationPath);

    CompletableFuture<UUID> putTagInList(UUID todoListID, UUID tagId);

    CompletableFuture<TodoList> removeTagFromList(UUID todoListID, UUID tagId);

    CompletableFuture<Tag> putTag(@NonNull Tag tag);

    CompletableFuture<Void> removeTag(UUID tagID);

    CompletableFuture<Tag> getTag(UUID tagID);

    CompletableFuture<List<UUID>> getTagsList();

    CompletableFuture<List<Tag>> getTagsFromIds(List<UUID> ids);
}
