package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.UUID;

public class ListSelectionController {
    private Database database;

    public ListSelectionController(Database database) {
        this.database = database;
    }

    /**
     * Creates a new todo-list and put it in the database
     *
     * @param title   The title of the todo-list
     */
    public UUID createTodoList(String title) {
        if (title == null) {
            throw new IllegalArgumentException();
        }
        try {
            TodoList tdl = new TodoList(title);
            UUID uuid = tdl.getId();
            database.putTodoList(tdl);
            return uuid;
        } catch (DatabaseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the title of a todo-list
     *
     * @param title The new title of the todo-list
     */
    public void updateTodoList(UUID todoListID, String title) {
        if (todoListID == null || title == null) {
            throw new IllegalArgumentException();
        }
        //To be completed when the necessary methods will be available in Database
    }

    /**
     * Removes a todo-list from the database
     *
     * @param todoListID The ID of the todo-list
     */
    public void removeTodoList(UUID todoListID) {
        try {
            database.removeTodoList(todoListID);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a todo-list from its ID
     *
     * @param todoListID The ID of the todo-list
     * @return The todo-list or null if the key does not exist in the database
     */
    public TodoList getTodoList(UUID todoListID) {
        TodoList result = null;
        try {
            result = database.getTodoList(todoListID);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
