package com.github.steroidteam.todolist.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.util.JSONSerializer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabaseTest {
    private static final String TODO_LIST_PATH = "/todo-lists/";
    private static final String NOTES_PATH = "/notes/";

    @Mock FirebaseFileStorageService storageServiceMock;

    CompletableFuture<byte[]> downloadFuture;
    CompletableFuture<String> uploadFuture;
    CompletableFuture<String[]> listDirFuture;

    FirebaseDatabase database;

    @Before
    public void before() {
        downloadFuture = new CompletableFuture<>();
        uploadFuture = new CompletableFuture<>();
        listDirFuture = new CompletableFuture<>();

        database = new FirebaseDatabase(storageServiceMock);
    }

    @Test
    public void constructorRejectsNullStorageService() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(null);
                });
    }

    @Test
    public void putTodoListRejectsNullList() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).putTodoList(null);
                });
    }

    @Test
    public void getTodoListCollectionWorks() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        CompletableFuture<String[]> listDirFuture = new CompletableFuture<>();
        listDirFuture.complete(new String[] {uuid1.toString(), uuid2.toString(), uuid3.toString()});
        doReturn(listDirFuture).when(storageServiceMock).listDir(anyString());

        try {
            TodoListCollection actualCollection = database.getTodoListCollection().join();
            assertEquals(uuid1, actualCollection.getUUID(0));
            assertEquals(uuid2, actualCollection.getUUID(1));
            assertEquals(uuid3, actualCollection.getUUID(2));

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void updateTodoListWorks() {
        TodoList expectedTodoList = new TodoList("some random title");

        CompletableFuture<String> uploadFuture = new CompletableFuture<>();
        uploadFuture.complete("some random path");
        doReturn(uploadFuture).when(storageServiceMock).upload(any(byte[].class), anyString());

        try {
            TodoList actualTodoList =
                    database.updateTodoList(UUID.randomUUID(), expectedTodoList).join();
            assertEquals(expectedTodoList, actualTodoList);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void putTodoListWorks() {
        final TodoList todoList = new TodoList("My list");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedFuture).when(storageServiceMock).upload(any(), eq(expectedPath));

        // Try to add a valid list.
        try {
            assertEquals(todoList, database.putTodoList(todoList).join());
        } catch (Exception e) {
            fail();
        }

        verify(storageServiceMock).upload(serializedList, expectedPath);
    }

    @Test
    public void removeTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    database.removeTodoList(null);
                });
    }

    @Test
    public void removeTodoListWorks() {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully removing the file.
        final CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
        doReturn(completedFuture).when(storageServiceMock).delete(expectedPath);

        // Try to remove a list.
        try {
            database.removeTodoList(todoListID).join();
        } catch (Exception e) {
            fail();
        }

        verify(storageServiceMock).delete(expectedPath);
    }

    @Test
    public void getTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).getTodoList(null);
                });
    }

    @Test
    public void getTodoListWorks() {
        final TodoList todoList = new TodoList("My list");
        final Task FIXTURE_TASK_1 = new Task("Buy bananas");
        final Task FIXTURE_TASK_2 = new Task("Eat bananas");
        todoList.addTask(FIXTURE_TASK_1);
        todoList.addTask(FIXTURE_TASK_2);
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully downloading the file.
        final CompletableFuture<byte[]> completedFuture =
                CompletableFuture.completedFuture(serializedList);
        doReturn(completedFuture).when(storageServiceMock).download(expectedPath);

        // Try to get a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageServiceMock);
        try {
            final TodoList fetchedList = database.getTodoList(todoList.getId()).get();

            verify(storageServiceMock).download(expectedPath);
            assertEquals(todoList, fetchedList);
            assertEquals(todoList.getSize(), fetchedList.getSize());
            assertEquals(todoList.getDate().getTime(), fetchedList.getDate().getTime());
            for (int i = 0; i < todoList.getSize(); i++) {
                assertEquals(todoList.getTask(i), fetchedList.getTask(i));
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void putTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).putTask(null, new Task("Some task"));
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).putTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void putTaskWorks() {
        final TodoList todoList = new TodoList("My list");
        final Task FIXTURE_TASK_1 = new Task("Buy bananas");
        final Task FIXTURE_TASK_2 = new Task("Eat bananas");
        todoList.addTask(FIXTURE_TASK_1);
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully downloading the file.
        final byte[] serializedOriginalList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<byte[]> completedDownloadFuture =
                CompletableFuture.completedFuture(serializedOriginalList);
        doReturn(completedDownloadFuture).when(storageServiceMock).download(expectedPath);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedUploadFuture).when(storageServiceMock).upload(any(), eq(expectedPath));

        // Try to put a task in a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageServiceMock);
        database.putTask(todoList.getId(), FIXTURE_TASK_2);

        // Add the new task to the list, to make it look like what we would expect to be stored
        // in the database.
        todoList.addTask(FIXTURE_TASK_2);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(storageServiceMock).download(expectedPath);
        verify(storageServiceMock).upload(serializedNewList, expectedPath);
    }

    @Test
    public void removeTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).removeTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).putTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void removeTaskWorks() {
        final TodoList todoList = new TodoList("My list");
        final Task FIXTURE_TASK_1 = new Task("Buy bananas");
        todoList.addTask(FIXTURE_TASK_1);
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully downloading the file.
        final byte[] serializedOriginalList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<byte[]> completedDownloadFuture =
                CompletableFuture.completedFuture(serializedOriginalList);
        doReturn(completedDownloadFuture).when(storageServiceMock).download(expectedPath);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedUploadFuture).when(storageServiceMock).upload(any(), eq(expectedPath));

        // Try to remove a task from valid list.
        database.removeTask(todoList.getId(), 0);

        // Remote the task from list, to make it look like what we would expect to be stored
        // in the database.
        todoList.removeTask(0);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(storageServiceMock).download(expectedPath);
        verify(storageServiceMock).upload(serializedNewList, expectedPath);
    }

    @Test
    public void getNoteWorks() {
        Note expectedNote = new Note("Some random title");

        byte[] serializedNote =
                JSONSerializer.serializeNote(expectedNote).getBytes(StandardCharsets.UTF_8);
        downloadFuture.complete(serializedNote);

        doReturn(downloadFuture).when(storageServiceMock).download(anyString());

        try {
            Note note = database.getNote(UUID.randomUUID()).join();
            assertEquals(note.getContent(), expectedNote.getContent());
            assertEquals(note.getId(), expectedNote.getId());
            assertEquals(note.getTitle(), expectedNote.getTitle());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void putNoteWorks() {
        Note expectedNote = new Note("Some random title");

        uploadFuture.complete("some path");
        doReturn(uploadFuture).when(storageServiceMock).upload(any(byte[].class), anyString());

        try {
            Note note = database.putNote(UUID.randomUUID(), expectedNote).join();
            assertEquals(expectedNote, note);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void updateNoteRejectsNullArgs() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).updateNote(null, new Note("title"));
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).updateNote(UUID.randomUUID(), null);
                });
    }

  @Test
    public void removeNoteRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    database.removeNote(null);
                });
    }

    @Test
    public void removeNoteWorks() {
        final UUID noteID = UUID.randomUUID();
        final String expectedPath = NOTES_PATH + noteID.toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully removing the file.
        final CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
        doReturn(completedFuture).when(storageServiceMock).delete(expectedPath);

        // Try to remove a list.
        try {
            database.removeNote(noteID).join();
        } catch (Exception e) {
            fail();
        }

        verify(storageServiceMock).delete(expectedPath);
    }

    @Test
    public void updateNoteRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    database.updateNote(null, new Note("TITLE"));
                });
        assertThrows(
                NullPointerException.class,
                () -> {
                    database.updateNote(UUID.randomUUID(), null);
                });
    }

    @Test
    public void updateNoteWorks() {
        Note expectedNote = new Note("some random note title");

        CompletableFuture<String> uploadFuture = new CompletableFuture<>();
        uploadFuture.complete("some random path");
        doReturn(uploadFuture).when(storageServiceMock).upload(any(byte[].class), anyString());

        try {
            Note actualNote = database.updateNote(UUID.randomUUID(), expectedNote).join();
            assertEquals(expectedNote, actualNote);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getNotesListWorks() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        listDirFuture.complete(new String[] {uuid1.toString(), uuid2.toString(), uuid3.toString()});
        doReturn(listDirFuture).when(storageServiceMock).listDir(anyString());

        final FirebaseDatabase database = new FirebaseDatabase(storageServiceMock);
        try {
            List<UUID> actualList = database.getNotesList().join();
            assertEquals(uuid1, actualList.get(0));
            assertEquals(uuid2, actualList.get(1));
            assertEquals(uuid3, actualList.get(2));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void setTaskDoneWorks() {
        TodoList expectedTodoList = new TodoList("some random title");
        Task task1 = new Task("Task 1");
        Task task2 = new Task("Task 2");
        expectedTodoList.addTask(task1);
        expectedTodoList.addTask(task2);

        byte[] serializedTodoList =
                JSONSerializer.serializeTodoList(expectedTodoList).getBytes(StandardCharsets.UTF_8);
        downloadFuture.complete(serializedTodoList);

        uploadFuture.complete("Some file path");

        doReturn(downloadFuture).when(storageServiceMock).download(anyString());
        doReturn(uploadFuture).when(storageServiceMock).upload(any(byte[].class), anyString());

        try {
            Task task = database.setTaskDone(UUID.randomUUID(), 1, true).join();
            assertTrue(task.isDone());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).getTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageServiceMock).getTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void getTaskWorks() {
        final TodoList todoList = new TodoList("My list");
        final Task FIXTURE_TASK_1 = new Task("Buy bananas");
        final Task FIXTURE_TASK_2 = new Task("Eat bananas");
        todoList.addTask(FIXTURE_TASK_1);
        todoList.addTask(FIXTURE_TASK_2);
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully downloading the file.
        final CompletableFuture<byte[]> completedFuture =
                CompletableFuture.completedFuture(serializedList);
        doReturn(completedFuture).when(storageServiceMock).download(expectedPath);

        // Try to get a valid task.
        try {
            final Task fetchedTask = database.getTask(todoList.getId(), 1).get();
            verify(storageServiceMock).download(expectedPath);
            assertEquals(FIXTURE_TASK_2, fetchedTask);
        } catch (Exception e) {
            fail();
        }
    }
}
