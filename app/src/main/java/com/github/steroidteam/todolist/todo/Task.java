package com.github.steroidteam.todolist.todo;

import java.io.Serializable;

/**
 * A class that represents a task described by a title and a body.
 * <p>
 * The index is null upon the creation of the Task, and it will
 * be assigned by the database when it's pushed into it.
 */
public class Task {

    private String body;
    private Integer index;

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
        this.index = null;
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

    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Returns the index of the task.
     *
     * @return Integer The index or null if not present in a to-do list.
     */
    public Integer getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Task{" +
                "body='" + body + "\'" +
                "}";
    }
}
