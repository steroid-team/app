package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.todo.Task;

/**
 * A adapter to store task in a database.
 * The key of the mapping is manage by an IdManager.
 */
public class TaskDatabaseAdapter {

    private final Database database;
    private IdManager idManager;

    /**
     * Constructs the Task Adapter.
     *
     * @param database The database where the tasks are stored.
     * @param idManager The id manager.
     */
    public TaskDatabaseAdapter(Database database, IdManager idManager) {
        if (database == null || idManager == null) {
            throw new IllegalArgumentException();
        }
        this.database = database;
        this.idManager = idManager;
    }

    /**
     * Pushes a Task into the database.
     * @param task The Task to push.
     */
    public void putTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException();
        }

        Integer newId = idManager.getNewId();
        task.setId(newId);
        this.database.put(newId.toString(), task.toString());
    }

    /**
     * Updates a task already in the database.
     *
     * @param newTask The current task with a new body.
     * @throws DatabaseException Thrown if the task isn't in the database.
     */
    public void updateTask(Task newTask) throws DatabaseException {
        if (newTask == null || newTask.getId() == null) {
            throw new IllegalArgumentException();
        }

        if (this.database.get(newTask.getId().toString()) != null) {
            this.database.put(newTask.getId().toString(), newTask.toString());
        } else {
            throw new DatabaseException("The task is not present in the database!");
        }
    }

    /**
     * Removes a task from the database
     * @param id the key of the mapping to remove.
     * @throws DatabaseException Thrown if the mapping is not in the database.
     */
    public void removeTask(Integer id) throws DatabaseException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        try {
            this.database.remove(id.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Gets the task associated to the given ID.
     *
     * @param id The key of the mapping.
     * @return The new task with the same ID and Body than the one in the database.
     */
    public Task getTask(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        String taskInString = this.database.get(id.toString());

        if (taskInString == null) {
            return null;
        }

        String[] args = taskInString.split("'");
        String stringId = args[1];
        String body = args[3];

        Task res = new Task(body);
        res.setId(Integer.valueOf(stringId));

        return res;
    }
}
