package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.todo.Task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void taskCorrectlyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Task nullTask = new Task("0", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Task nullTask = new Task(null, "This is a body !");
        });
    }

    @Test
    public void taskIsCorrectlyCreated() {
        String body = "Body!";
        String id = "89273";
        Task dumbTask = new Task(id, body);

        assertEquals(id, dumbTask.getId());
        assertEquals(body, dumbTask.getBody());

        Task dumbTask2 = new Task(id, "This is a different body !");

        assertTrue(dumbTask.equals(dumbTask));
        assertFalse(dumbTask.equals(null));
        assertFalse(dumbTask.equals("Different Class"));
        assertTrue(dumbTask.equals(dumbTask2));
    }

    @Test
    public void modifyWorks() {
        String newBody = "New Body!";
        Task dumbTask = new Task("0", "body");
        dumbTask.modifyBody(newBody);

        assertEquals(newBody, dumbTask.getBody());
    }

    @Test
    public void serializationOfaTaskIsCorrect() {
        String body = "Body!";
        String id = "89273";
        Task dumbTask = new Task("89273", "Body!");

        assertEquals("Task{id='89273', body='Body!'}", dumbTask.toString());
    }
}
