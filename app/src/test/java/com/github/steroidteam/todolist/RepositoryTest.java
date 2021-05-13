package com.github.steroidteam.todolist;

import android.content.Context;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.database.TodoListRepository;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTest {

    private final File fakeFile = new File("fake pathname");

    @Mock
    Database localDatabase;

    @Mock
    Database remoteDatabase;

    @Mock
    Context contextMock;

    @Before
    public void setDatabaseMock() {
        doReturn(fakeFile).when(contextMock).getCacheDir();
    }

    @Test
    public void verifySyncDataWithSameList() {
        TodoListCollection todoListCollection = new TodoListCollection();
        todoListCollection.addUUID(UUID.randomUUID());
        todoListCollection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionCompletableFuture = new CompletableFuture<>();
        todoListCollectionCompletableFuture.complete(todoListCollection);

        doReturn(todoListCollectionCompletableFuture).when(localDatabase).getTodoListCollection();

        TodoList todoList = new TodoList("A TITLE");
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todoList);

        doReturn(todoListCompletableFuture).when(localDatabase).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture).when(remoteDatabase).getTodoList(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        TodoListRepository todoListRepository = new TodoListRepository(contextMock);

        
    }
}
