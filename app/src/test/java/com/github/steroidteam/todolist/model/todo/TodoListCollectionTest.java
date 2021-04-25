package com.github.steroidteam.todolist.model.todo;

import com.github.steroidteam.todolist.todo.TodoListCollection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TodoListCollectionTest {

    TodoListCollection collection;

    @Before
    public void before() {
        collection = new TodoListCollection();
    }

    @Test
    public void addTodoList() {
        assertEquals(0, collection.getSize());
        collection.addUUID(UUID.randomUUID());
        assertEquals(1, collection.getSize());
    }

    @Test
    public void getTodoListUUID() {
        UUID uuid = UUID.randomUUID();
        collection.addUUID(uuid);
        assertEquals(uuid, collection.getUUID(0));
    }

    @Test
    public void removeTodoListUUID() {
        UUID uuid = UUID.randomUUID();
        collection.addUUID(uuid);
        collection.removeUUID(uuid);
        assertEquals(0, collection.getSize());
    }
}
