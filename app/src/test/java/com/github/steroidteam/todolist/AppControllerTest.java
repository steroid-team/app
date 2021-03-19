package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.MVC.AppController;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.todo.TodoList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class AppControllerTest {

    @Test
    public void getTodoListReturnsCorrectList() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo1 = new TodoList("todo1");
        TodoList todo2 = new TodoList("todo2");
        TodoList todo3 = new TodoList("todo3");
        db.putTodoList(todo1);
        db.putTodoList(todo2);
        db.putTodoList(todo3);

        AppController controller = new AppController(db);

        assertEquals(todo2, controller.getTodoList(todo2.getId()));
        assertEquals(todo1, controller.getTodoList(todo1.getId()));
        assertEquals(todo3, controller.getTodoList(todo3.getId()));
    }

    @Test
    public void createTaskCorrectlyThrowsNullArgument() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo = new TodoList("todo");
        db.putTodoList(todo);
        AppController controller = new AppController(db);

        assertThrows(IllegalArgumentException.class, () -> {
            controller.createTask(todo.getId(), null);
        });
    }

    @Test
    public void createTaskCorrectlyCreatesTask() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo = new TodoList("todo");
        db.putTodoList(todo);
        AppController controller = new AppController(db);

        controller.createTask(todo.getId(), "test task");
        TodoList retrievedList = controller.getTodoList(todo.getId());
        assertEquals("test task", retrievedList.getTask(0).getBody());
    }

    @Test
    public void updateTaskDescriptionCorrectlyThrowsNullArgument() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo = new TodoList("todo");
        db.putTodoList(todo);
        AppController controller = new AppController(db);

        controller.createTask(todo.getId(), "test task");
        assertThrows(IllegalArgumentException.class, () -> {
            controller.updateTaskDescription(todo.getId(), 0, null);
        });
    }

    @Test
    public void updateTaskDescriptionWorks() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo = new TodoList("todo");
        db.putTodoList(todo);
        AppController controller = new AppController(db);

        controller.createTask(todo.getId(), "test task");
        controller.updateTaskDescription(todo.getId(), 0, "new description");
        TodoList retrievedList = controller.getTodoList(todo.getId());
        assertEquals("new description", retrievedList.getTask(0).getBody());
    }

    @Test
    public void removeTaskWorks() {
        VolatileDatabase db = new VolatileDatabase();
        TodoList todo = new TodoList("todo");
        db.putTodoList(todo);
        AppController controller = new AppController(db);

        controller.createTask(todo.getId(), "task 1");
        controller.createTask(todo.getId(), "task 2");
        controller.removeTask(todo.getId(), 0);
        TodoList retrievedList = controller.getTodoList(todo.getId());
        assertEquals("task 2", retrievedList.getTask(0).getBody());
        assertNull(retrievedList.getTask(1));
    }

}
