package com.github.steroidteam.todolist.task;

/**
 * A class that represents a task described by a title and a body.
 */
public class Task {

    private String title;
    private String body;
    private int id;

    /**
     * Constructs a new Task with a title and a body.
     *
     * @param id The id of the task.
     * @param title The title of the task.
     * @param body The body of the task.
     * @throws IllegalArgumentException Thrown if one or more arguments are null
     */
    public Task(int id, String title, String body) {
        if(title == null || body == null) {
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.title = title;
        this.body = body;
    }

    /**
     * Modifies the title of the task.
     * @param newTitle The new title.
     */
    public void modifyTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Modifies the body of the task.
     * @param newBody The new body.
     */
    public void modifyBody(String newBody) {
        this.body = newBody;
    }

    public String getTitle() {
        return new String(title);
    }

    public String getBody() {
        return new String(body);
    }
}
