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
     * @param todoListID The ID of the to-do list to remove.
     * @throws IllegalArgumentException Thrown if the argument is null.
     */
    void removeTodoList(Integer todoListID);

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
    void putTask(Integer todoListID, Task task);

    /**
     * Removes a task from the database.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task within the list.
     * @throws IllegalArgumentException Thrown if one argument is null.
     */
    void removeTask(Integer todoListID, Integer taskIndex);

    /**
     * Gets the task from the database
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex The index of the task you want from a to-do list.
     * @return The task or null if the key does not exist in the database.
     */
    Task getTask(Integer todoListID, Integer taskIndex);

}
