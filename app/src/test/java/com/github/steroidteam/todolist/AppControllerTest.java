package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.MVC.AppController;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.IdManager;
import com.github.steroidteam.todolist.database.TaskDatabaseAdapter;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.Task;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AppControllerTest {

    @Test
    public void createTaskCorrectlyThrowNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppController appController = new AppController(new VolatileDatabase(), new IdManager());
            appController.createTask(null);
        });
    }

    @Test
    public void createTaskCorrectlyCreatesTask() {
        Database db = new VolatileDatabase();
        AppController appController = new AppController(db, new IdManager());
        appController.createTask("test controller create task");
        assertEquals("Task{id='1', body='test controller create task'}", db.get("1"));
    }

}
