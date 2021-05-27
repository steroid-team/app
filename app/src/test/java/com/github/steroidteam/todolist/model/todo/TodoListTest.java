package com.github.steroidteam.todolist.model.todo;

import static org.junit.Assert.*;

import java.util.Date;
import org.junit.Test;

public class TodoListTest {

    @Test
    public void quickTodoListTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    TodoList t = new TodoList(null);
                });

        Task t1 = new Task("body1");
        Task t2 = new Task("body2");
        TodoList td = new TodoList("This is a title!");

        assertNotEquals(null, td);

        assertNotNull(td.getDate());
        // Make sure that the difference between the task's date and the current one is not
        // greater than 100ms.
        assertTrue((new Date().getTime() - td.getDate().getTime()) < 100);

        td.addTask(t1);
        assertEquals(1, td.getSize());

        td.removeTask(1);
        td.removeTask(-1);
        assertEquals(1, td.getSize());

        td.removeTask(0);
        assertEquals(0, td.getSize());
        assertTrue(td.isEmpty());

        assertNull(td.getTask(0));
        assertNull(td.getTask(-3));

        String newTitle = "new title !";
        td.setTitle(newTitle);
        assertEquals(newTitle, td.getTitle());

        td.addTask(t2);
        StringBuilder str = new StringBuilder();
        str.append("Todo-List{").append(t2.toString()).append(",").append("\n").append("}");
        assertEquals(str.toString(), td.toString());
    }

    @Test
    public void sortByDoneWorks() {
        TodoList todoList = new TodoList("todo");
        Task t1= new Task("1");
        Task t2= new Task("2");
        Task t3= new Task("3");

        t2.setDone(true);
        t3.setDone(true);

        todoList.addTask(t1);
        todoList.addTask(t2);
        todoList.addTask(t3);
        System.out.println(todoList.toString());
        System.out.println(todoList.sortByDone().toString());
    }
}
