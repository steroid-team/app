package com.github.steroidteam.todolist;

import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import org.junit.Test;

public class TodoRepositoryTest {

    @Test
    public void simpleRepoTest() {
        TodoRepository repo = new TodoRepository();

        LiveData<TodoList> ld = repo.getTodoList(repo.id);
        assertEquals(ld, repo.getTodoList(repo.id));
    }
}
