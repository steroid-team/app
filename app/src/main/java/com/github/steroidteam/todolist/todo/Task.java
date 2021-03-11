package com.github.steroidteam.todolist.todo;

import java.io.Serializable;

/**
 * A class that represents a task described by a title and a body.
 */
public class Task implements Serializable{

    private String body;
    private final String id;

    /**
     * Constructs a new Task with a title and a body.
     *
     * @param id The id of the task.
     * @param body The body of the task.
     * @throws IllegalArgumentException Thrown if one or more arguments are null
     */
    public Task(String id, String body) {
        if(id == null || body == null) {
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.body = body;
    }

    /**
     * Modifies the body of the task.
     * @param newBody The new body.
     */
    public void modifyBody(String newBody) {
        this.body = newBody;
    }

    /**
     * Returns the body of the task.
     * @return String The body.
     */
    public String getBody() {
        return new String(body);
    }

    public String getId() {return id;}

    @Override
    public String toString() {
        return "Task{" +
                    "id='" + id + "\'" +
                    ", body='" + body + "\'"+
                "}";
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        else if(o == null || this.getClass() != o.getClass()) {return false;}
        else {
            Task task = (Task) o;
            return this.id.equals(task.getId());
        }
     }
}
