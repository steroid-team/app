package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.IdManager;
import com.github.steroidteam.todolist.database.TaskDatabaseAdapter;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.Task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskDatabaseAdapterTest {

    @Test
    public void taskAdapterIsCorrectlyCreated() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TaskDatabaseAdapter(null, new IdManager());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new TaskDatabaseAdapter(new VolatileDatabase(), null);
        });
    }

    @Test
    public void putWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            adapter.putTask(null);
        });

        Database db = new VolatileDatabase();
        IdManager manager = new IdManager();

        TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(db, manager);
        Task t = new Task("task1");
        adapter.putTask(t);
        assertEquals(t.toString(), db.get(t.getId().toString()));
    }

    @Test
    public void removeWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            adapter.removeTask(null);
        });

        assertThrows(DatabaseException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            adapter.removeTask(23);
        });

        VolatileDatabase db = new VolatileDatabase();
        TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(db, new IdManager());
        Task t = new Task("task1");
        adapter.putTask(t);
        try {
            adapter.removeTask(t.getId());
            assertEquals(null, db.get(t.getId().toString()));
        } catch (Exception e) {
        }
    }

    @Test
    public void getTaskWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            adapter.getTask(null);
        });

        TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
        assertEquals(null, adapter.getTask(3));
        Task t = new Task("task1");
        adapter.putTask(t);

        assertEquals(t, adapter.getTask(t.getId()));
    }

    @Test
    public void updateCheck() {
        assertThrows(IllegalArgumentException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            adapter.updateTask(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            Task t = new Task("body");
            adapter.updateTask(t);
        });

        assertThrows(DatabaseException.class, () -> {
            TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(new VolatileDatabase(), new IdManager());
            Task t = new Task("body");
            t.setId(56);
            adapter.updateTask(t);
        });

        VolatileDatabase db = new VolatileDatabase();
        TaskDatabaseAdapter adapter = new TaskDatabaseAdapter(db, new IdManager());
        Task t = new Task("body");
        adapter.putTask(t);
        t.setBody("new body!");
        try {
            adapter.updateTask(t);
            assertEquals(t.toString(), db.get(t.getId().toString()));
        }catch (Exception e) {}

    }
}
