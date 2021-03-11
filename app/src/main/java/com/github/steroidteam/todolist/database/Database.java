package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;

/**
 * A database that stores pairs of key and value.
 */
public interface Database {

    /**
     * Pushes a new task in the database.
     *
     * @param task The task of the mapping.
     */
    void putTask(Task task);

    /**
     * Removes a task from the database.
     *
     * @param id The key of the mapping.
     * @throws DatabaseException Thrown if the id is not in the database
     */
    void removeTask(int id) throws DatabaseException;

    /**
     * Updates a task from the database
     *
     * @param updatedTask The updated task to put in the database.
     * @throws DatabaseException Thrown if the id of the task is not in the database
     */
    void updateTask(Task updatedTask) throws DatabaseException;

    /**
     * Gets a task from the database
     *
     * @param id The key of the mapping.
     * @return The task of the mapping or null if the key does not exist in the database.
     * @throws DatabaseException Thrown if the id is not in the database
     */
    Task getTask(int id) throws DatabaseException;

}
