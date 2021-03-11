package com.github.steroidteam.todolist.MVC;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.IdManager;
import com.github.steroidteam.todolist.database.TaskDatabaseAdapter;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.Task;

/**
 * The controller of the App that communicates with the View and the Database
 */
public class AppController {
    private TaskDatabaseAdapter taskDatabaseAdapter;
    // TODO Add the view so it can update it

    public AppController(Database database, IdManager manager){
        taskDatabaseAdapter = new TaskDatabaseAdapter(database, manager);
    }

    /**
     * Create a new Task and put it in the database
     *
     * @param body   The body of the task
     */
    public void createTask(String body) {
        if (body == null) {
            throw new IllegalArgumentException();
        }
        taskDatabaseAdapter.putTask(new Task(body));
    }

    /**
     * Update a task and put it in the database
     *
     * @param bodyNewTask   The new body of the task
     */
    // It must know which task it should update and we can't do that yet
    /*
    public void updateTask(String bodyNewTask) {
        if (bodyNewTask == null) {
            throw new IllegalArgumentException();
        }
        try {
            taskDatabaseAdapter.updateTask(new Task(bodyNewTask));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
    */
    // TODO removeTask since it needs to know which id the view has assigned to the task I don't know how to do it for now

    // TODO getTask same problem as removeTask

}
