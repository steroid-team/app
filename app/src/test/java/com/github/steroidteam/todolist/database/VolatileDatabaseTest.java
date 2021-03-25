package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.notes.Note;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class VolatileDatabaseTest {

    @Test
    public void databaseExceptionOk() {
        assertThrows(DatabaseException.class, () -> {
            throw new DatabaseException("This is a exception!");
        });
    }

    @Test
    public void todoWorks() {
        TodoList todo = new TodoList("A title!");
        VolatileDatabase database = new VolatileDatabase();

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.putTodoList(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.removeTodoList(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTodoList(null);
        });

        database.putTodoList(todo);
        assertEquals(todo, database.getTodoList(todo.getId()));

        database.removeTodoList(todo.getId());
        assertNull(database.getTodoList(todo.getId()));
    }

    @Test
    public void putTaskWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.putTask(null, new Task("Body!"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.putTask(UUID.randomUUID(), null);
        });

        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();

        database.putTask(todo.getId(), task);
        assertNull(todo.getTask(0));

        database.putTodoList(todo);
        database.putTask(todo.getId(), task);
        assertEquals(todo.getTask(0), task);
    }

    @Test
    public void removeTaskWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.removeTask(null, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.removeTask(UUID.randomUUID(), null);
        });

        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();
        database.putTodoList(todo);
        database.putTask(todo.getId(), task);

        database.removeTask(todo.getId(), 2);
        assertEquals(1, todo.getSize());

        database.removeTask(UUID.randomUUID(), 0);
        assertEquals(1, todo.getSize());

        database.removeTask(todo.getId(), 0);
        assertEquals(0, todo.getSize());
    }

    @Test
    public void getTaskWorks() {
        // Null arguments throw IllegalArgumentException.
        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTask(null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTask(null, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTask(UUID.randomUUID(), null);
        });

        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();
        database.putTodoList(todo);
        database.putTask(todo.getId(), task);

        // Getting tasks from a nonexistent list returns a null object.
        assertNull(database.getTask(UUID.randomUUID(), 0));

        // Getting non-existent tasks from an existing list returns a null object.
        assertNull(database.getTask(todo.getId(),todo.getSize() + 1));

        // Getting existent tasks from an existing list returns the corresponding Task.
        assertEquals(task, database.getTask(todo.getId(), 0));
    }

    @Test
    public void noteWorks() {
        Note note = new Note("A title!");
        VolatileDatabase database = new VolatileDatabase();

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.putNote(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.removeNote(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getNote(null);
        });

        database.putNote(note);
        assertEquals(note, database.getNote(note.getId()));

        database.removeNote(note.getId());
        assertNull(database.getTodoList(note.getId()));


    }

}
