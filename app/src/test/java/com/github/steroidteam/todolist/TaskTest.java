package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.task.Task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void taskCorrectlyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Task nullTask = new Task(0, null, "body");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Task nullTask = new Task(0, "title", null);
        });
    }

    @Test
    public void taskIsCorrectlyCreated() {
        String title = "Title";
        String body = "Body!";
        Task dumbTask = new Task(0, title, body);

        assertEquals(title, dumbTask.getTitle());
        assertEquals(body, dumbTask.getBody());
    }

    @Test
    public void modifyWorks() {
        String newTitle = "New Title";
        String newBody = "New Body!";
        Task dumbTask = new Task(0, "title", "body");

        dumbTask.modifyTitle(newTitle);
        dumbTask.modifyBody(newBody);

        assertEquals(newTitle, dumbTask.getTitle());
        assertEquals(newBody, dumbTask.getBody());
    }
}
