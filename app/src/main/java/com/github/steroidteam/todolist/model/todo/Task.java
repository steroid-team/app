package com.github.steroidteam.todolist.model.todo;

import com.google.android.gms.maps.model.LatLng;
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
    private LatLng remindAtLocation;
    private String locationName;

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
        this.remindAtLocation = null;
    }

    public Task setBody(String newBody) {
        Objects.requireNonNull(newBody);
        this.body = newBody;
        return this;
    }

    public String getBody() {
        return this.body;
    }

    public void setRemindAtLocation(LatLng location, String locationName) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(locationName);

        this.remindAtLocation = location;
        this.locationName = locationName;
    }

    public LatLng getRemindAtLocation() {
        return this.remindAtLocation;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public void removeLocationReminder() {
        this.remindAtLocation = null;
        this.locationName = null;
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

    public Task setDone(Boolean done) {
        this.done = done;
        return this;
    }

    public Task setDueDate(Date dueDate) {
        Objects.requireNonNull(dueDate);
        this.dueDate = dueDate;
        return this;
    }

    public Task removeDueDate() {
        this.dueDate = null;
        return this;
    }

    public Date getDueDate() {
        return this.dueDate;
    }
}
