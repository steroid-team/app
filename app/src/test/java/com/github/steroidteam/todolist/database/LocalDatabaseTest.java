package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.util.JSONSerializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalDatabaseTest {

    private final File testFile = new File("fileForTesting_canBeDeleted");
    private static final String TODO_LIST_PATH = "/todo-lists/";
    private static final String NOTES_PATH = "/notes/";

    @Mock
    LocalFileStorageService localFileStorageService;

    @Test
    public void constructorRejectsNullStorageService() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(null);
                });
    }

    @Test
    public void putTodoListRejectsNullList() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).putTodoList(null);
                });
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
        when(localFileStorageService.upload(serializedList, expectedPath)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(todoList, database.putTodoList(todoList).get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).upload(serializedList, expectedPath);
    }

    @Test
    public void removeTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).removeTodoList(null);
                });
    }

    @Test
    public void removeTodoListWorks() throws DatabaseException {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully removing the file.
        final CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
        when(localFileStorageService.delete(expectedPath)).thenReturn(completedFuture);

        // Try to remove a list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        database.removeTodoList(todoListID);

        verify(localFileStorageService).delete(expectedPath);
    }

    @Test
    public void removeTodoListThrowsDatabaseExceptionOnError() {
        final UUID todoListID = UUID.randomUUID();
        final String expectedPath = TODO_LIST_PATH + todoListID.toString() + ".json";

        // Return a future that simulates an error during the deletion.
        final CompletableFuture<Void> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException());
        when(localFileStorageService.delete(expectedPath)).thenReturn(failingFuture);

        final LocalDatabase database = new LocalDatabase(localFileStorageService);

        assertThrows(DatabaseException.class, () -> database.removeTodoList(todoListID));
        verify(localFileStorageService).delete(expectedPath);
    }

    @Test
    public void getTodoListRejectsNullTodoListID() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).getTodoList(null);
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedFuture);

        // Try to get a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            final TodoList fetchedList = database.getTodoList(todoList.getId()).get();

            verify(localFileStorageService).download(expectedPath);
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
                    new LocalDatabase(localFileStorageService).putTask(null, new Task("Some task"));
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).putTask(UUID.randomUUID(), null);
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedDownloadFuture);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(any(), eq(expectedPath))).thenReturn(completedUploadFuture);

        // Try to put a task in a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        database.putTask(todoList.getId(), FIXTURE_TASK_2);

        // Add the new task to the list, to make it look like what we would expect to be stored
        // in the database.
        todoList.addTask(FIXTURE_TASK_2);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(localFileStorageService).download(expectedPath);
        verify(localFileStorageService).upload(serializedNewList, expectedPath);
    }

    @Test
    public void removeTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).removeTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).putTask(UUID.randomUUID(), null);
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedDownloadFuture);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedUploadFuture =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(any(), eq(expectedPath))).thenReturn(completedUploadFuture);

        // Try to remove a task from valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        database.removeTask(todoList.getId(), 0);

        // Remote the task from list, to make it look like what we would expect to be stored
        // in the database.
        todoList.removeTask(0);
        final byte[] serializedNewList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        verify(localFileStorageService).download(expectedPath);
        verify(localFileStorageService).upload(serializedNewList, expectedPath);
    }

    @Test
    public void getTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).getTask(null, 0);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).getTask(UUID.randomUUID(), null);
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedFuture);

        // Try to get a valid task.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);

        try {
            final Task fetchedTask = database.getTask(todoList.getId(), 1).get();
            verify(localFileStorageService).download(expectedPath);
            assertEquals(FIXTURE_TASK_2, fetchedTask);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void renameTaskRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).renameTask(null, 0, "name");
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).renameTask(UUID.randomUUID(), null, "name");
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).renameTask(UUID.randomUUID(), 0, null);
                });
    }

    @Test
    public void renameTaskWorks() {
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedFuture);

        todoList.renameTask(0, "new name");
        final byte[] serializedList2 =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully renaming a task.
        final CompletableFuture<String> completedFuture2 =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(serializedList2, expectedPath)).thenReturn(completedFuture2);

        // Try to get a valid task.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);

        try {
            assertEquals(todoList.getTask(0), database.renameTask(todoList.getId(), 0, "new name").get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).upload(serializedList2, expectedPath);
    }

    @Test
    public void updateTodoListRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).updateTodoList(UUID.randomUUID(), null);
                });

        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).updateTodoList(null, new TodoList("A todo!"));
                });
    }

    @Test
    public void updateTodoListWorks() {

        final TodoList todoList = new TodoList("My list");

        final String expectedPath = TODO_LIST_PATH + todoList.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully updating the to-do.
        final CompletableFuture<String> completedFuture =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(serializedList, expectedPath)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(todoList, database.updateTodoList(todoList.getId(), todoList).get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).upload(serializedList, expectedPath);
    }

    @Test
    public void putNoteRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).putNote(null);
                });
    }

    @Test
    public void putNoteWorks() {

        final Note note = new Note("My note");
        final String expectedPath = NOTES_PATH + note.getId().toString() + ".json";
        final byte[] serializedList =
                JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<String> completedFuture =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(serializedList, expectedPath)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(note, database.putNote(note).get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).upload(serializedList, expectedPath);
    }

    @Test
    public void getNoteRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).getNote(null);
                });
    }

    @Test
    public void getNoteWorks() {

        final Note note = new Note("My note");
        final String expectedPath = NOTES_PATH + note.getId().toString() + ".json";
        final byte[] serializedNote =
                JSONSerializer.serializeNote(note).getBytes(StandardCharsets.UTF_8);

        // Return a future like the one that the FirebaseFileStorageService would produce after
        // successfully uploading the file.
        final CompletableFuture<byte[]> completedFuture =
                CompletableFuture.completedFuture(serializedNote);
        when(localFileStorageService.download(expectedPath)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(note.getId(), database.getNote(note.getId()).get().getId());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).download(expectedPath);
    }

    @Test
    public void setTaskDoneRejectsNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalDatabase(localFileStorageService).setTaskDone(null, 0, false);
                });
    }

    @Test
    public void setTaskDoneWorks() {
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
        when(localFileStorageService.download(expectedPath)).thenReturn(completedFuture);

        todoList.getTask(0).setDone(true);
        final byte[] serializedList2 =
                JSONSerializer.serializeTodoList(todoList).getBytes(StandardCharsets.UTF_8);

        final CompletableFuture<String> completedFuture2 =
                CompletableFuture.completedFuture(expectedPath);
        when(localFileStorageService.upload(serializedList2, expectedPath)).thenReturn(completedFuture2);

        // Try to get a valid task.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);

        try {
            assertEquals(todoList.getTask(0), database.setTaskDone(todoList.getId(), 0, true).get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).upload(serializedList2, expectedPath);
    }

    @Test
    public void getNotesListWorks() {

        final Note note = new Note("My note");
        final Note note2 = new Note("My note 2");
        final List<UUID> expectedList = new ArrayList<>();
        expectedList.add(note.getId());
        expectedList.add(note2.getId());

        String[] list = {note.getId().toString(), note2.getId().toString()};
        final CompletableFuture<String[]> completedFuture =
                CompletableFuture.completedFuture(list);
        when(localFileStorageService.listDir(NOTES_PATH)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(expectedList, database.getNotesList().get());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).listDir(NOTES_PATH);
    }

    @Test
    public void getTodoListCollectionWorks() {

        final TodoList todo = new TodoList("My list");
        final TodoList todo2 = new TodoList("My list2");
        final TodoListCollection expectedCollection = new TodoListCollection();
        expectedCollection.addUUID(todo.getId());
        expectedCollection.addUUID(todo2.getId());

        String[] list = {todo.getId().toString(), todo2.getId().toString()};
        final CompletableFuture<String[]> completedFuture =
                CompletableFuture.completedFuture(list);
        when(localFileStorageService.listDir(TODO_LIST_PATH)).thenReturn(completedFuture);

        // Try to add a valid list.
        final LocalDatabase database = new LocalDatabase(localFileStorageService);
        try {
            assertEquals(expectedCollection.toString(), database.getTodoListCollection().get().toString());
        } catch (Exception e) {
            fail();
        }

        verify(localFileStorageService).listDir(TODO_LIST_PATH);
    }
}
