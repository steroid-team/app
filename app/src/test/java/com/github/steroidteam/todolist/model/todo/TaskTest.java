package com.github.steroidteam.todolist.model.todo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

public class TaskTest {

    @Test
    public void taskRejectsNullArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    new Task(null);
                });
    }

    @Test
    public void taskIsCorrectlyCreated() {
        String body = "Body!";
        Task dumbTask = new Task(body);

        assertEquals(body, dumbTask.getBody());

        Task dumbTask2 = new Task("This is a different body !");

        assertEquals(dumbTask, dumbTask);
        assertNotEquals(null, dumbTask);
        assertNotEquals("Different Class", dumbTask);
        assertNotEquals(dumbTask, dumbTask2);
    }

    @Test
    public void getWorks() {
        String newBody = "New Body!";
        Task dumbTask = new Task("body");
        dumbTask.setBody(newBody);

        assertEquals(newBody, dumbTask.getBody());
    }

    @Test
    public void serializationOfaTaskIsCorrect() {
        String body = "Body!";
        Task dumbTask = new Task(body);

        assertEquals("Task{body='Body!', done=false}", dumbTask.toString());
    }

    @Test
    public void equalsWorks() {
        assertEquals(new Task("Do something"), new Task("Do something"));
        assertNotEquals(new Task("Do something"), new Task("Do something else"));
        assertNotEquals(null, new Task(""));
    }
}
