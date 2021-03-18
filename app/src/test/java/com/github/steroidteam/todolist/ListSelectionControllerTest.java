package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.todo.Task;

import org.junit.Test;

import static org.junit.Assert.*;

public class ListSelectionControllerTest {

    private Database database;
    private ListSelectionController lsc = new ListSelectionController(database);

    @Test
    public void CreateTodoListWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            lsc.createTodoList(null);
        });
    }
}