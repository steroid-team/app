package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import org.junit.Test;

import static org.junit.Assert.*;

public class TodoListTest {

    @Test
    public void quickTodoListTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            TodoList t = new TodoList(null);
        });

        Task t1 = new Task("body1");
        Task t2 = new Task("body2");
        t2.setId(98);
        TodoList td = new TodoList("This is a title!");
        td.addTask(t1);
        assertEquals(1, td.getSize());
        td.removeTask(0);
        assertEquals(0, td.getSize());
        assertTrue(td.isEmpty());
        Integer hash = td.getDate().toString().hashCode();
        assertEquals(hash, td.getId());
        String newTitle = "new title !";
        td.setTitle(newTitle);
        assertEquals(newTitle, td.getTitle());

        td.addTask(t2);
        StringBuilder str = new StringBuilder();
        str.append("Todo-List{").append(t2.toString()).append(",").append("\n").append("}");
        assertEquals(str.toString(), td.toString());
    }
}
