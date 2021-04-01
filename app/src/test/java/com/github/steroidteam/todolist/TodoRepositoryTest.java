package com.github.steroidteam.todolist;

<<<<<<< HEAD
import androidx.lifecycle.LiveData;

import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;

=======
import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;
import com.github.steroidteam.todolist.database.TodoRepository;
import com.github.steroidteam.todolist.todo.TodoList;
>>>>>>> cfb7ad8ae27bdf352a8b940ea222365a6ab87dc8
import org.junit.Test;

public class TodoRepositoryTest {

    @Test
    public void simpleRepoTest() {
        TodoRepository repo = new TodoRepository();

        LiveData<TodoList> ld = repo.getTodoList(repo.id);
        assertEquals(ld, repo.getTodoList(repo.id));
    }
}
