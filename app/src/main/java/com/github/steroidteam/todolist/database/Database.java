package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

/**
 * A database that stores pairs of key and value.
 */
public interface Database {

    /**
     * Pushes a new to-do list in the database.
     *
     * @param list The to-do list to push.
     */
    void putTodoList(TodoList list);

    /**
     * Removes a to-do list from the database.
     *
     * @param id The ID of the to-do list to remove.
     * @throws IllegalArgumentException Thrown if the argument is null.
     * @throws DatabaseException        Thrown if the id is not in the database
     */
    void removeTodoList(Integer id);

    /**
     * Gets the to-do list given the ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if the ID isn't in the database.
     */
    TodoList getTodoList(Integer todoListID);

    /**
     * Pushes a new task in the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task The new task to add.
     */
    void pushTask(Integer todoListID, Task task);

    /**
     * Removes a task from the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskID The id (also index) of the task within the list.
     * @throws IllegalArgumentException Thrown if one argument is null.
     * @throws DatabaseException        Thrown if one id is not in the database
     */
    void removeTask(Integer todoListID, Integer taskID);

    /**
     * Gets the task from the database
     *
     * @param taskID The ID of the task you want.
     * @return The task or null if the key does not exist in the database.
     */
    Task getTask(Integer taskID);

}
