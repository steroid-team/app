package com.github.steroidteam.todolist.model.todo;

import java.util.Date;
import java.util.Objects;

/**
 * A class that represents a task described by a title and a body.
 *
 * <p>The index is null upon the creation of the Task, and it will be assigned by the database when
 * it's pushed into it.
 */
public class Task {

    private String body;
    private Boolean done;
    private Date dueDate;

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
        this.done = false;
        this.dueDate = null;
    }

    public void setBody(String newBody) {
        Objects.requireNonNull(newBody);
        this.body = newBody;
    }

    public String getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "Task{"
                + "body='"
                + body
                + "', "
                + "done="
                + done
                + ", "
                + "dueDate="
                + dueDate
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(body, task.body)
                && (done == task.isDone())
                && Objects.equals(dueDate, task.getDueDate());
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setDueDate(Date dueDate) {
        Objects.requireNonNull(dueDate);
        this.dueDate = dueDate;
    }

    public void removeDueDate() {
        this.dueDate = null;
    }

    public Date getDueDate() {
        return this.dueDate;
    }
}
