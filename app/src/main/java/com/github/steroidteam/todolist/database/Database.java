package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.notes.Note;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

/**
 * A database that stores pairs of key and value.
 */
public interface Database {

    /**
     * Pushes a new to-do list in the database.
     *
     * @param list The to-do list to push.
     */
    void putTodoList(TodoList list) throws DatabaseException;

    /**
     * Removes a to-do list from the database.
     *
     * @param todoListID The ID of the to-do list to remove.
     * @throws IllegalArgumentException Thrown if the argument is null.
     */
    void removeTodoList(UUID todoListID) throws DatabaseException;

    /**
     * Gets the to-do list given the ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if the ID isn't in the database.
     */
    TodoList getTodoList(UUID todoListID) throws DatabaseException;

    /**
     * Pushes a new task in the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task The new task to add.
     */
    void putTask(UUID todoListID, Task task) throws DatabaseException;

    /**
     * Removes a task from the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the list.
     * @throws IllegalArgumentException Thrown if one argument is null.
     */
    void removeTask(UUID todoListID, Integer taskIndex) throws DatabaseException;

    /**
     * Gets the task from the database
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task you want from a to-do list.
     * @return The task or null if the key does not exist in the database.
     */
    Task getTask(UUID todoListID, Integer taskIndex) throws DatabaseException;

    /**
     * Pushes a new note in the database
     *
     * @param note The new note to add.
     */
    void putNote(Note note) throws DatabaseException;

    /**
     * Removes a note from the database.
     *
     * @param noteID The ID of the note to remove.
     * @throws IllegalArgumentException Thrown if the argument is null.
     */
    void removeNote(UUID noteID) throws DatabaseException;

    /**
     * Gets the note given the ID.
     *
     * @param noteID The ID of the note.
     * @return The note or null if the ID isn't in the database.
     */
    Note getNote(UUID noteID) throws DatabaseException;

}
