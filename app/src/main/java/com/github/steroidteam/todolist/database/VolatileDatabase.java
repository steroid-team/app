package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

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
    public void putTodoList(TodoList list) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void removeTodoList(Integer id) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public TodoList getTodoList(Integer todoListID) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void pushTask(Integer todoListID, Task task) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void removeTask(Integer todoListID, Integer taskID) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Task getTask(Integer taskID) {
        throw new UnsupportedOperationException("TODO");
    }
}
