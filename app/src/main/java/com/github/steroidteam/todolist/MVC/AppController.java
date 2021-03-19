package com.github.steroidteam.todolist.MVC;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

/**
 * The controller of the App that communicates with the View and the Database
 */
public class AppController {

    private Database database;
    // TODO Add the view so it can update it

    public AppController(Database database) {
        this.database = database;
    }

    /**
     *
     * @param todoListID The ID of the TodoList
     * @return The to-do list or null if the ID isn't in the database.
     */
    public TodoList getTodoList(UUID todoListID) {
        TodoList res = null;
        try {
            res = database.getTodoList(todoListID);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Creates a new Task and put it in the database
     *
     * @param todoListID The ID of the TodoList
     * @param description The description of the task
     */
    public void createTask(UUID todoListID, String description) {
        if (description == null) {
            throw new IllegalArgumentException();
        }
        try {
            database.putTask(todoListID, new Task(description));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the description of a task
     *
     * @param todoListID The ID of the TodoList
     * @param taskIndex The index of the Task in the given TodoList
     * @param description The new description of the task
     */
    public void updateTaskDescription(UUID todoListID, Integer taskIndex, String description) {
        if (description == null) {
            throw new IllegalArgumentException();
        }

        // The database doesn't support updates yet => temporary implementation
        try {
            database.removeTask(todoListID, taskIndex);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        createTask(todoListID, description);
    }

    /**
     * Removes a task from the database
     *
     * @param todoListID The ID of the TodoList
     * @param taskIndex The index of the Task in the given TodoList
     */
    public void removeTask(UUID todoListID, Integer taskIndex) {
        try {
            database.removeTask(todoListID, taskIndex);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

}
