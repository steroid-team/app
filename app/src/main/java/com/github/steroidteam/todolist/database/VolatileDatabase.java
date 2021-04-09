package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A simple volatile implementation of the interface database. The database is just a HashMap
 * storing the ID of to-do lists for keys, and objects TodoList for values.
 */
public class VolatileDatabase implements Database {

    private Map<UUID, TodoList> database;

    /** Creates a volatile database. */
    public VolatileDatabase() {
        this.database = new HashMap<>();
    }

    @Override
    public CompletableFuture<TodoListCollection> getTodoListCollection() {
        return null;
    }

    /**
     * Pushes the to-do list in the database. Updates the to-do list if it's already present in the
     * database (=if there is already a same ID).
     *
     * @param list The to-do list to push.
     */
    @Override
    public CompletableFuture<String> putTodoList(TodoList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }

        database.put(list.getId(), list);

        return new CompletableFuture<>();
    }

    /**
     * Removes the to-do list from the database. Do nothing id the ID is not a key.
     *
     * @param id The id of the to-do list.
     */
    @Override
    public void removeTodoList(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        database.remove(id);
    }

    /**
     * Gets a to-do list given an ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if not present in the database.
     */
    @Override
    public CompletableFuture<TodoList> getTodoList(UUID todoListID) {
        if (todoListID == null) {
            throw new IllegalArgumentException();
        }
        CompletableFuture<TodoList> future = new CompletableFuture<>();
        future.complete(database.get(todoListID));
        return future;
    }

    /**
     * Pushes a task in the database. Assign the correct ID to the task (equivalent to its index in
     * the to-do list). Updates the task if it's already present in the database. Do nothing if the
     * ID is not is the database or if the task is already present in a to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task The new task to add.
     */
    @Override
    public void putTask(UUID todoListID, Task task) {
        if (todoListID == null || task == null) {
            throw new IllegalArgumentException();
        }
        // get the to-do list from the database.
        TodoList list = database.get(todoListID);
        if (list != null) {
            list.addTask(task);
        }
    }

    /**
     * Removes a task from the database. Do nothing if the ID of the to-do is not present in the
     * database, or if the task is not in the to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the to-do list.
     */
    @Override
    public void removeTask(UUID todoListID, Integer taskIndex) {
        if (todoListID == null || taskIndex == null) {
            throw new IllegalArgumentException();
        }
        TodoList list = database.get(todoListID);
        if (list != null) {
            list.removeTask(taskIndex);
        }
    }

    /**
     * Gets a task given a to-do list ID and a task index.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the to-do list.
     * @return Return null if the to-do list or the task are not present in the database.
     */
    @Override
    public Task getTask(UUID todoListID, Integer taskIndex) {
        if (todoListID == null || taskIndex == null) {
            throw new IllegalArgumentException();
        }
        TodoList list = database.get(todoListID);
        if (list != null) {
            return list.getTask(taskIndex);
        }
        return null;
    }

    public void renameTask(UUID todoListID, Integer taskIndex, String newBody) {
        TodoList list = database.get(todoListID);
        if (list != null) {
            list.renameTask(taskIndex, newBody);
        }
    }
}
