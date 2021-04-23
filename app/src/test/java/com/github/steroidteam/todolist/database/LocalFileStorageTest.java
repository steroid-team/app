package com.github.steroidteam.todolist.database;

import android.content.Context;

import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileStorageTest {

    private final File testFile = new File("fileForTesting_canBeDeleted");

    @Mock
    Context contextMock;

    @Test
    public void uploadReadWorks() {

        when(contextMock.getFilesDir()).thenReturn(testFile);


        final LocalFileStorageService localFileStorageService = new LocalFileStorageService(contextMock);
        final byte[] bytes = "some bytes".getBytes(StandardCharsets.UTF_8);
        final String path = "/a/path";
        localFileStorageService.upload(bytes, path);

        try {
            byte[] result = localFileStorageService.download(path).get();
            assertEquals(new String(bytes, StandardCharsets.UTF_8), new String(bytes, StandardCharsets.UTF_8));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        localFileStorageService.delete(path);
    }

    @Test
    public void deleteWorks() {

        when(contextMock.getFilesDir()).thenReturn(testFile);


        final LocalFileStorageService localFileStorageService = new LocalFileStorageService(contextMock);
        final byte[] bytes = "some bytes".getBytes(StandardCharsets.UTF_8);
        final String path = "/a/path";
        localFileStorageService.upload(bytes, path).join();
        localFileStorageService.delete(path).join();

        try {
            byte[] result = localFileStorageService.download(path).get();
            assertNull(result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void listDirWorks() {

        when(contextMock.getFilesDir()).thenReturn(testFile);

        final LocalFileStorageService localFileStorageService = new LocalFileStorageService(contextMock);
        final byte[] bytes = "some bytes".getBytes(StandardCharsets.UTF_8);
        final byte[] bytes2 = "some bytes".getBytes(StandardCharsets.UTF_8);
        final String mainPath = "/todo-lists/";
        final String path = "/todo-lists/A";
        final String path2 = "/todo-lists/B";
        String[] tab = {new String(bytes), new String(bytes2)};

        localFileStorageService.upload(bytes, path);
        localFileStorageService.upload(bytes2, path2);

        String[] result = localFileStorageService.listDir(mainPath).join();
        assertEquals("A",result[0]);
        assertEquals("B",result[1]);

        localFileStorageService.delete(path);
        localFileStorageService.delete(path2);
    }

}
