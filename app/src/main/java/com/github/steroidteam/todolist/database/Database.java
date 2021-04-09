package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import com.github.steroidteam.todolist.todo.TodoListCollection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** A database that stores pairs of key and value. */
public interface Database {

    /**
     * Returns the unique to-do list collection of the current user
     *
     * @return The unique to-do list collection.
     */
    CompletableFuture<TodoListCollection> getTodoListCollection();

    /**
     * Pushes a new to-do list in the database.
     *
     * @param list The to-do list to push.
     */
    CompletableFuture<String> putTodoList(TodoList list);

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
    CompletableFuture<TodoList> getTodoList(UUID todoListID);

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
}
