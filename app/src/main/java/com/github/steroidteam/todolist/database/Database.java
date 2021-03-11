package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;

/**
 * A database that stores pairs of key and value.
 */
public interface Database {

    /**
     * Pushes a new value in the database.
     *
     * @param key   The key of the mapping
     * @param value The value of the mapping.
     */
    void put(String key, String value);

    /**
     * Removes a task from the database.
     *
     * @param key The key of the mapping.
     * @throws IllegalArgumentException Thrown if the argument is null.
     * @throws DatabaseException        Thrown if the id is not in the database
     */
    void remove(String key) throws DatabaseException;

    /**
     * Gets a value from the database
     *
     * @param key The key of the mapping.
     * @return The value of the mapping or null if the key does not exist in the database.
     */
    String get(String key);

}
