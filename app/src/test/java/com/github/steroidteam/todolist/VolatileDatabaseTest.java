package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import org.junit.Test;

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
            database2.putTask(0, null);
        });


        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();

        database.putTask(0, task);
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
            database2.removeTask(0, null);
        });

        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();
        database.putTodoList(todo);
        database.putTask(todo.getId(), task);

        database.removeTask(todo.getId(), 2);
        assertEquals(1, todo.getSize());

        database.removeTask(todo.getId(), 0);
        assertEquals(0, todo.getSize());
    }

    @Test
    public void getTaskWorks() {
        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTask(null, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase database2 = new VolatileDatabase();
            database2.getTask(0, null);
        });

        TodoList todo = new TodoList("A title!");
        Task task = new Task("A body!");
        VolatileDatabase database = new VolatileDatabase();
        database.putTodoList(todo);
        database.putTask(todo.getId(), task);
    }
}
