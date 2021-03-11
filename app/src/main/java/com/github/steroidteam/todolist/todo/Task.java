package com.github.steroidteam.todolist.todo;

import java.io.Serializable;

/**
 * A class that represents a task described by a title and a body.
 * <p>
 * The id is null upon the creation of the Task, and it will
 * be assigned by the database when it's pushed into it.
 */
public class Task implements Serializable {

    private String body;
    private Integer id;

    /**
     * Constructs a new Task with a body.
     *
     * @param body The body of the task.
     * @throws IllegalArgumentException Thrown if one or more arguments are null
     */
    public Task(String body) {
        if (body == null) {
            throw new IllegalArgumentException();
        }
        this.body = body;
        this.id = null;
    }

    /**
     * Modifies the body of the task.
     *
     * @param newBody The new body.
     */
    public void setBody(String newBody) {
        this.body = newBody;
    }

    /**
     * Returns the body of the task.
     *
     * @return String The body.
     */
    public String getBody() {
        return new String(body);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the id of the task.
     *
     * @return Integer The id or null if not present in the database.
     */
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id.toString() + "\'" +
                ", body='" + body + "\'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || this.getClass() != o.getClass()) {
            return false;
        } else {
            Task task = (Task) o;
            return this.id.equals(task.getId());
        }
    }
}
