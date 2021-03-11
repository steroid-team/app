package com.github.steroidteam.todolist.database;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple volatile implementation of the interface database.
 * The database is just a HashMap.
 */
public class VolatileDatabase implements Database {

    private Map<String, String> database;

    public VolatileDatabase() {
        this.database = new HashMap<>();
    }

    @Override
    public void put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        database.put(key, value);
    }

    @Override
    public void remove(String key) throws DatabaseException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!database.containsKey(key)) {
            throw new DatabaseException("The key isn't in the database!");
        } else {
            database.remove(key);
        }
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!database.containsKey(key)) {
            return null;
        }
        return database.get(key);
    }
}
