package com.github.steroidteam.todolist.model.todo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import android.location.Location;
import android.location.LocationManager;
import java.util.Date;
import org.junit.Test;

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
        assertEquals(false, dumbTask.isDone());
    }

    @Test
    public void setBodyWorks() {
        String newBody = "New Body!";
        Task dumbTask = new Task("body");
        dumbTask.setBody(newBody);

        assertEquals(newBody, dumbTask.getBody());
    }

    @Test
    public void dueDateWorks() {
        Task dumbTask = new Task("body");
        // The due date is null by default.
        assertNull(dumbTask.getDueDate());

        Date date = new Date();
        dumbTask.setDueDate(date);

        assertEquals(date, dumbTask.getDueDate());

        dumbTask.removeDueDate();

        assertNull(dumbTask.getDueDate());
    }

    @Test
    public void serializationOfaTaskIsCorrect() {
        String body = "Body!";
        Task dumbTask = new Task(body);

        assertEquals("Task{body='Body!', done=false, dueDate=null}", dumbTask.toString());

        dumbTask.setDone(true);
        Date dueDate = new Date();
        dumbTask.setDueDate(dueDate);
        assertEquals(
                "Task{body='Body!', done=true, dueDate=" + dueDate.toString() + "}",
                dumbTask.toString());
    }

    @Test
    public void equalsWorks() {
        Task task1 = new Task("Do something");
        Task task2 = new Task("Do something");

        assertEquals(task1, task2);

        task1.setDone(false);
        task2.setDone(true);

        assertNotEquals(task1, task2);

        task1.setDone(true);
        task2.setDone(true);

        assertEquals(task1, task2);

        task1.setDueDate(new Date());

        assertNotEquals(task1, task2);

        assertNotEquals(null, new Task(""));
    }

    @Test
    public void setLocationReminderWorks() {
        Task task = new Task("Some random title");
        assertNull(task.getLocationReminder());
        task.setLocationReminder(new Location(LocationManager.GPS_PROVIDER));
        assertNotNull(task.getLocationReminder());
    }
}
