package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;

import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.todo.TodoListCollection;
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
     * Renames a task in the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the list.
     */
    CompletableFuture<Task> renameTask(UUID todoListID, Integer taskIndex, String newName);

    /**
     * Gets the task from the database
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
     * Sets "done" flag in a task
     *
     * @param todoListID id of the todolist containing the task
     * @param index of the task inside the todolist
     * @param isDone the state of the flag to set
     * @return the updated task
     */
    CompletableFuture<Task> setTaskDone(UUID todoListID, int index, boolean isDone);
}
