package com.github.steroidteam.todolist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.database.NoteRepository;
import com.github.steroidteam.todolist.database.TodoListRepository;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTest {

    private final File fakeFile = new File("fake pathname");

    @Mock Database localDatabase;

    @Mock Database remoteDatabase;

    @Mock Context contextMock;

    @Before
    public void setDatabaseMock() {
        doReturn(fakeFile).when(contextMock).getCacheDir();
        CompletableFuture<List<Tag>> tagsFuture = new CompletableFuture<>();
        tagsFuture.complete(new ArrayList<>());
    }

    @Test
    public void verifySyncDataWithSameListAndSameTime() {
        TodoListCollection todoListCollection = new TodoListCollection();
        todoListCollection.addUUID(UUID.randomUUID());
        todoListCollection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionCompletableFuture =
                new CompletableFuture<>();
        todoListCollectionCompletableFuture.complete(todoListCollection);

        doReturn(todoListCollectionCompletableFuture).when(localDatabase).getTodoListCollection();
        doReturn(todoListCollectionCompletableFuture).when(remoteDatabase).getTodoListCollection();

        TodoList todoList = new TodoList("A TITLE");
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todoList);

        doReturn(todoListCompletableFuture).when(localDatabase).getTodoList(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        Long sameTime = 4L;
        CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();
        longCompletableFuture.complete(sameTime);
        doReturn(longCompletableFuture).when(localDatabase).getLastModifiedTimeTodo(any());
        doReturn(longCompletableFuture).when(remoteDatabase).getLastModifiedTimeTodo(any());

        TodoListRepository todoListRepository = new TodoListRepository(contextMock);

        // 2 times in fetchData() -> setTodoListMutableLiveData.
        // 2 times in checkLastModified()
        verify(localDatabase, times(6)).getTodoList(any());
    }

    @Test
    public void verifySyncDataWithListNotPresentLocally() {
        TodoListCollection todoListCollection = new TodoListCollection();
        todoListCollection.addUUID(UUID.randomUUID());
        todoListCollection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionCompletableFuture =
                new CompletableFuture<>();
        todoListCollectionCompletableFuture.complete(todoListCollection);

        TodoListCollection todoListCollection2 = new TodoListCollection();
        todoListCollection2.addUUID(UUID.randomUUID());
        todoListCollection2.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionCompletableFuture2 =
                new CompletableFuture<>();
        todoListCollectionCompletableFuture2.complete(todoListCollection2);

        doReturn(todoListCollectionCompletableFuture).when(localDatabase).getTodoListCollection();
        doReturn(todoListCollectionCompletableFuture2).when(remoteDatabase).getTodoListCollection();

        TodoList todoList = new TodoList("A TITLE");
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todoList);

        doReturn(todoListCompletableFuture).when(localDatabase).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture).when(remoteDatabase).getTodoList(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        TodoListRepository todoListRepository = new TodoListRepository(contextMock);

        verify(remoteDatabase, times(1)).getTodoListCollection();
        verify(remoteDatabase, times(2)).getTodoList(any());
        verify(localDatabase, times(2)).removeTodoList(any());
    }

    @Test
    public void verifySyncDataDifferentTime() {
        TodoListCollection todoListCollection = new TodoListCollection();
        todoListCollection.addUUID(UUID.randomUUID());
        todoListCollection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionCompletableFuture =
                new CompletableFuture<>();
        todoListCollectionCompletableFuture.complete(todoListCollection);

        doReturn(todoListCollectionCompletableFuture).when(localDatabase).getTodoListCollection();
        doReturn(todoListCollectionCompletableFuture).when(remoteDatabase).getTodoListCollection();

        TodoList todoList = new TodoList("A TITLE");
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todoList);

        doReturn(todoListCompletableFuture).when(localDatabase).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture).when(remoteDatabase).getTodoList(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        Long localTime = 4L;
        CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();
        longCompletableFuture.complete(localTime);
        doReturn(longCompletableFuture).when(localDatabase).getLastModifiedTimeTodo(any());
        Long remoteTime = 10L;
        CompletableFuture<Long> longCompletableFuture2 = new CompletableFuture<>();
        longCompletableFuture2.complete(remoteTime);
        doReturn(longCompletableFuture2).when(remoteDatabase).getLastModifiedTimeTodo(any());

        TodoListRepository todoListRepository = new TodoListRepository(contextMock);

        // 2 times in checkLastModified()
        verify(remoteDatabase, times(2)).getTodoList(any());
    }

    @Test
    public void NOTE_REPO_verifySyncDataWithSameListAndSameTime() {
        ArrayList<UUID> noteCollection = new ArrayList<>();
        noteCollection.add(UUID.randomUUID());
        noteCollection.add(UUID.randomUUID());
        CompletableFuture<ArrayList<UUID>> noteListFuture = new CompletableFuture<>();
        noteListFuture.complete(noteCollection);

        doReturn(noteListFuture).when(localDatabase).getNotesList();
        doReturn(noteListFuture).when(remoteDatabase).getNotesList();

        Note note = new Note("A TITLE");
        CompletableFuture<Note> noteCompletableFuture = new CompletableFuture<>();
        noteCompletableFuture.complete(note);

        doReturn(noteCompletableFuture).when(localDatabase).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        Long sameTime = 4L;
        CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();
        longCompletableFuture.complete(sameTime);
        doReturn(longCompletableFuture).when(localDatabase).getLastModifiedTimeNote(any());
        doReturn(longCompletableFuture).when(remoteDatabase).getLastModifiedTimeNote(any());

        NoteRepository noteRepository = new NoteRepository(contextMock);

        // 2 times in fetchData() -> setTodoListMutableLiveData.
        // 2 times in checkLastModified()
        verify(localDatabase, times(6)).getNote(any());
    }

    @Test
    public void NOTE_REPO_verifySyncDataWithListNotPresentLocally() {
        ArrayList<UUID> noteCollection = new ArrayList<>();
        noteCollection.add(UUID.randomUUID());
        noteCollection.add(UUID.randomUUID());
        CompletableFuture<ArrayList<UUID>> noteListFuture = new CompletableFuture<>();
        noteListFuture.complete(noteCollection);

        ArrayList<UUID> noteCollection2 = new ArrayList<>();
        noteCollection2.add(UUID.randomUUID());
        noteCollection2.add(UUID.randomUUID());
        CompletableFuture<ArrayList<UUID>> noteListFuture2 = new CompletableFuture<>();
        noteListFuture2.complete(noteCollection2);

        doReturn(noteListFuture).when(localDatabase).getNotesList();
        doReturn(noteListFuture2).when(remoteDatabase).getNotesList();

        Note note = new Note("A TITLE");
        CompletableFuture<Note> noteCompletableFuture = new CompletableFuture<>();
        noteCompletableFuture.complete(note);

        doReturn(noteCompletableFuture).when(localDatabase).getNote(any(UUID.class));
        doReturn(noteCompletableFuture).when(remoteDatabase).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        NoteRepository noteRepository = new NoteRepository(contextMock);

        verify(remoteDatabase, times(1)).getNotesList();
        verify(remoteDatabase, times(2)).getNote(any());
        verify(localDatabase, times(2)).removeNote(any());
    }

    @Test
    public void NOTE_REPO_verifySyncDataDifferentTime() {
        ArrayList<UUID> noteCollection = new ArrayList<>();
        noteCollection.add(UUID.randomUUID());
        noteCollection.add(UUID.randomUUID());
        CompletableFuture<ArrayList<UUID>> noteListFuture = new CompletableFuture<>();
        noteListFuture.complete(noteCollection);

        doReturn(noteListFuture).when(localDatabase).getNotesList();
        doReturn(noteListFuture).when(remoteDatabase).getNotesList();

        Note note = new Note("A TITLE");
        CompletableFuture<Note> noteCompletableFuture = new CompletableFuture<>();
        noteCompletableFuture.complete(note);

        doReturn(noteCompletableFuture).when(localDatabase).getNote(any(UUID.class));
        doReturn(noteCompletableFuture).when(remoteDatabase).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabaseDiff(localDatabase, remoteDatabase);

        Long localTime = 4L;
        CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();
        longCompletableFuture.complete(localTime);
        doReturn(longCompletableFuture).when(localDatabase).getLastModifiedTimeNote(any());
        Long remoteTime = 10L;
        CompletableFuture<Long> longCompletableFuture2 = new CompletableFuture<>();
        longCompletableFuture2.complete(remoteTime);
        doReturn(longCompletableFuture2).when(remoteDatabase).getLastModifiedTimeNote(any());

        NoteRepository noteRepository = new NoteRepository(contextMock);

        // 2 times in checkLastModified()
        verify(remoteDatabase, times(2)).getNote(any());
    }
}
