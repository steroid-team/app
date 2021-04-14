package com.github.steroidteam.todolist.database;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple volatile implementation of the interface database. The database is just a HashMap
 * storing the ID of to-do lists for keys, and objects TodoList for values.
 */
public class VolatileDatabase implements Database {

    private final ArrayList<TodoList> database;

    /** Creates a volatile database. */
    public VolatileDatabase() {
        this.database = new ArrayList<>();
    }

    /**
     * Pushes the to-do list in the database. Updates the to-do list if it's already present in the
     * database (=if there is already a same ID).
     *
     * @param list The to-do list to push.
     */
    @Override
    public void putTodoList(TodoList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        database.add(list);
    }

    /**
     * Removes the to-do list from the database. Do nothing id the ID is not a key.
     *
     * @param id The id of the to-do list.
     */
    @Override
    public void removeTodoList(@NonNull UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < database.size(); ++i) {
            if (database.get(i).getId() == id) {
                database.remove(i);
                i = database.size();
            }
        }
    }

    /**
     * Gets a to-do list given an ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if not present in the database.
     */
    @Override
    public TodoList getTodoList(@NonNull UUID todoListID) {
        if (todoListID == null) {
            throw new IllegalArgumentException();
        }
        TodoList tl;
        for (int i = 0; i < database.size(); ++i) {
            tl = database.get(i);
            if (tl.getId() == todoListID) {
                return tl;
            }
        }
        return null;
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
        // get the to-do list from the database.
        TodoList tl = findTodoList(todoListID, task);
        if (tl != null) {
            tl.addTask(task);
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
        // get the to-do list from the database.
        TodoList tl = findTodoList(todoListID, taskIndex);
        if (tl != null) {
            tl.removeTask(taskIndex);
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
        TodoList tl = findTodoList(todoListID, taskIndex);
        if (tl != null) {
            return tl.getTask(taskIndex);
        }
        return null;
    }

    /**
     * Rename a task
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the to-do list.
     * @param newBody The new body of the task to modify
     */
    public void renameTask(UUID todoListID, Integer taskIndex, String newBody) {
        TodoList tl = getTodoList(todoListID);
        if (tl != null) {
            tl.renameTask(taskIndex, newBody);
        }
    }

    /**
     * Rename the title of a to-do list
     *
     * @param todoListID The id of the to-do list
     * @param newTitle The new title of the to-do
     */
    public void renameTodo(UUID todoListID, String newTitle) {
        TodoList tl = getTodoList(todoListID);
        if (tl != null) {
            tl.setTitle(newTitle);
        }
    }

    /**
     * Return all the todo-list,
     *
     * @return the array list containing all the to-do lists
     */
    public ArrayList<TodoList> getAllTodo() {
        return this.database;
    }

    /**
     * Helper to check if args not null and find a specific todo list
     *
     * @param todoListID The id of the to-do list we want
     * @param o task index or task object (only check if it is not null)
     * @return the to-do list with UUID todoListID
     */
    private TodoList findTodoList(UUID todoListID, Object o) {
        if (todoListID == null || o == null) {
            throw new IllegalArgumentException();
        }
        return getTodoList(todoListID);
    }
}
