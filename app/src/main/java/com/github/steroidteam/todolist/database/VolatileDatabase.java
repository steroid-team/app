package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple volatile implementation of the interface database.
 * The database is just a HashMap storing the ID of to-do lists for keys,
 * and objects TodoList for values.
 */
public class VolatileDatabase implements Database {

    private Map<Integer, TodoList> database;

    /**
     * Creates a volatile database.
     */
    public VolatileDatabase() {
        this.database = new HashMap<>();
    }

    /**
     * Pushes the to-do list in the database.
     * Updates the to-do list if it's already present in the database
     * (=if there is already a same ID).
     *
     * @param list The to-do list to push.
     */
    @Override
    public void putTodoList(TodoList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }

        database.put(list.getId(), list);
    }

    /**
     * Removes the to-do list from the database.
     * Do nothing id the ID is not a key.
     *
     * @param id The id of the to-do list.
     */
    @Override
    public void removeTodoList(Integer id) {
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
    public TodoList getTodoList(Integer todoListID) {
        if (todoListID == null) {
            throw new IllegalArgumentException();
        }
        return database.get(todoListID);
    }

    /**
     * Pushes a task in the database.
     * Assign the correct ID to the task (equivalent to its index in the to-do list).
     * Updates the task if it's already present in the database.
     * Do nothing if the ID is not is the database or if the task is already present in a to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task       The new task to add.
     */
    @Override
    public void putTask(Integer todoListID, Task task) {
        if (todoListID == null || task == null || task.getIndex() != null) {
            throw new IllegalArgumentException();
        }
        //get the to-do list from the database.
        TodoList list = database.get(todoListID);
        if (list != null) {
            //recover the size to assign the index to the task.
            Integer index = list.getSize();
            task.setIndex(index);
            list.addTask(task);
        }
    }

    /**
     * Removes a task from the database.
     * Do nothing if the ID of the to-do is not present in the database,
     * or if the task is not in the to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex  The index of the task within the to-do list.
     */
    @Override
    public void removeTask(Integer todoListID, Integer taskIndex) {
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
     * @param taskIndex  The index of the task within the to-do list.
     * @return Return null if the to-do list or the task are not present in the database.
     */
    @Override
    public Task getTask(Integer todoListID, Integer taskIndex) {
        if (todoListID == null || taskIndex == null) {
            throw new IllegalArgumentException();
        }
        TodoList list = database.get(todoListID);
        if (list != null) {
            return list.getTask(taskIndex);
        }
        return null;
    }
}
