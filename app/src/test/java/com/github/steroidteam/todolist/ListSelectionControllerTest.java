package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ListSelectionControllerTest {

    private VolatileDatabase database = new VolatileDatabase();
    private ListSelectionController lsc = new ListSelectionController(database);

    @Test
    public void CreateTodoListRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            lsc.createTodoList(null);
        });
    }

    @Test
    public void CreateTodoListWorks() {
        UUID uuid = lsc.createTodoList("Title");
        String expectedTitle = database.getTodoList(uuid).getTitle();
        assertEquals("Title", expectedTitle);
    }

    @Test
    public void UpdateTodoListRejectsNullUuid() {
        assertThrows(IllegalArgumentException.class, () -> {
            lsc.updateTodoList(null, "Title");
        });
    }

    @Test
    public void UpdateTodoListRejectsNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            lsc.updateTodoList(UUID.randomUUID(), null);
        });
    }

    @Test
    public void RemoveTodoListWorks() {
        TodoList tdl = new TodoList("Title");
        UUID uuid = tdl.getId();
        database.putTodoList(tdl);
        assertEquals(uuid, database.getTodoList(uuid).getId());
        lsc.removeTodoList(uuid);
        assertEquals(null, database.getTodoList(uuid));
    }

    @Test
    public void GetTodoListWorks() {
        TodoList tdl = new TodoList("Title");
        UUID uuid = tdl.getId();
        assertEquals(database.getTodoList(uuid), lsc.getTodoList(uuid));
    }
}