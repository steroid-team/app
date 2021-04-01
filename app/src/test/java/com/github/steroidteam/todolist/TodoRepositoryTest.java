package com.github.steroidteam.todolist;

import androidx.lifecycle.LiveData;

import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;

import org.junit.Test;

import static org.junit.Assert.*;

public class TodoRepositoryTest {

    @Test
    public void simpleRepoTest() {
        TodoRepository repo = new TodoRepository();

        LiveData<TodoList> ld = repo.getTodoList(repo.id);
        assertEquals(ld, repo.getTodoList(repo.id));
    }
}
