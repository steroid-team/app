package com.github.steroidteam.todolist.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.util.JSONSerializer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabaseTest {
    private static final String TODO_LIST_PATH = "/todo-lists";

    @Mock FirebaseFileStorageService storageService;

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
                    new FirebaseDatabase(storageService).putTodoList(null);
                });
    }

    @Test
    public void putTodoListWorks() throws DatabaseException {
        final TodoList todoList = new TodoList("My list");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedFuture).when(storageService).upload(any(), eq(expectedPath));

        // Try to add a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        database.putTodoList(todoList);

        verify(storageService).upload(serializedList, expectedPath);
    }

    @Test
    public void putTodoListThrowsDatabaseExceptionOnError() {
        final TodoList todoList = new TodoList("My list");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future that simulates an error during the upload.
        final CompletableFuture<String> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).upload(any(), eq(expectedPath));

        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(DatabaseException.class, () -> database.putTodoList(todoList));
        verify(storageService).upload(serializedList, expectedPath);
    }

    @Test
    public void removeTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).removeTodoList(null);
                });
    }

    @Test
    public void removeTodoListWorks() throws DatabaseException {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully removing the file.
        final CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
        doReturn(completedFuture).when(storageService).delete(expectedPath);

        // Try to remove a list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        database.removeTodoList(todoListID);

        verify(storageService).delete(expectedPath);
    }

    @Test
    public void removeTodoListThrowsDatabaseExceptionOnError() {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future that simulates an error during the deletion.
        final CompletableFuture<Void> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).delete(expectedPath);

        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(DatabaseException.class, () -> database.removeTodoList(todoListID));
        verify(storageService).delete(expectedPath);
    }

    @Test
    public void getTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).getTodoList(null);
                });
    }

    @Test
    public void getTodoListWorks() throws DatabaseException {
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
        doReturn(completedFuture).when(storageService).download(expectedPath);

        // Try to get a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        final TodoList fetchedList = database.getTodoList(todoList.getId());

        verify(storageService).download(expectedPath);
        assertEquals(todoList, fetchedList);
        assertEquals(todoList.getSize(), fetchedList.getSize());
        assertEquals(todoList.getDate().getTime(), fetchedList.getDate().getTime());
        for (int i = 0; i < todoList.getSize(); i++) {
            assertEquals(todoList.getTask(i), fetchedList.getTask(i));
        }
    }

    @Test
    public void getTodoListThrowsDatabaseExceptionOnError() {
        final TodoList todoList = new TodoList("My list");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";

        // Return a future that simulates an error during the download.
        final CompletableFuture<byte[]> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).download(expectedPath);

        // Try to get a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(DatabaseException.class, () -> database.getTodoList(todoList.getId()));
        verify(storageService).download(expectedPath);
    }

    @Test
    public void putTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).putTask(null, new Task("Some task"));
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).putTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void putTaskWorks() throws DatabaseException {
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
        doReturn(completedDownloadFuture).when(storageService).download(expectedPath);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedUploadFuture).when(storageService).upload(any(), eq(expectedPath));

        // Try to put a task in a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        database.putTask(todoList.getId(), FIXTURE_TASK_2);

        // Add the new task to the list, to make it look like what we would expect to be stored
        // in the database.
        todoList.addTask(FIXTURE_TASK_2);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(storageService).download(expectedPath);
        verify(storageService).upload(serializedNewList, expectedPath);
    }

    @Test
    public void putTaskThrowsDatabaseExceptionOnError() {
        final TodoList todoList = new TodoList("My list");
        final Task FIXTURE_TASK_1 = new Task("Buy bananas");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";

        // Return a future that simulates an error during the download.
        final CompletableFuture<byte[]> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).download(expectedPath);

        // Try to put a task in a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(
                DatabaseException.class, () -> database.putTask(todoList.getId(), FIXTURE_TASK_1));
        verify(storageService).download(expectedPath);
    }

    @Test
    public void removeTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).removeTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).putTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void removeTaskWorks() throws DatabaseException {
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
        doReturn(completedDownloadFuture).when(storageService).download(expectedPath);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        doReturn(completedUploadFuture).when(storageService).upload(any(), eq(expectedPath));

        // Try to remove a task from valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        database.removeTask(todoList.getId(), 0);

        // Remote the task from list, to make it look like what we would expect to be stored
        // in the database.
        todoList.removeTask(0);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(storageService).download(expectedPath);
        verify(storageService).upload(serializedNewList, expectedPath);
    }

    @Test
    public void removeTaskThrowDatabaseExceptionOnError() {
        final TodoList todoList = new TodoList("My list");
        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";

        // Return a future that simulates an error during the download.
        final CompletableFuture<byte[]> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).download(expectedPath);

        // Try to remove a task from a valid list.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(DatabaseException.class, () -> database.removeTask(todoList.getId(), 0));
        verify(storageService).download(expectedPath);
    }

    @Test
    public void getTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).getTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new FirebaseDatabase(storageService).getTask(UUID.randomUUID(), null);
                });
    }

    @Test
    public void getTaskWorks() throws DatabaseException {
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
        doReturn(completedFuture).when(storageService).download(expectedPath);

        // Try to get a valid task.
        final FirebaseDatabase database = new FirebaseDatabase(storageService);
        final Task fetchedTask = database.getTask(todoList.getId(), 1);

        verify(storageService).download(expectedPath);
        assertEquals(FIXTURE_TASK_2, fetchedTask);
    }

    @Test
    public void getTaskThrowsDatabaseExceptionOnError() {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future that simulates an error during the download.
        final CompletableFuture<Void> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        doReturn(failingFuture).when(storageService).download(expectedPath);

        final FirebaseDatabase database = new FirebaseDatabase(storageService);

        assertThrows(DatabaseException.class, () -> database.getTask(todoListID, 0));
        verify(storageService).download(expectedPath);
    }
}
