package com.github.steroidteam.todolist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocalFileStorageTest {

    private final String PATH = "test-data/";
    private LocalFileStorageService localFileStorageService;
    private Context appContext;

    @Before
    public void initFileStorage() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        localFileStorageService = new LocalFileStorageService(appContext);
    }

    @After
    public void removeAllTestFiles() {
        File[] files = appContext.getFilesDir().listFiles();
        if (files != null) {
            for (File f1 : files) {
                // LEVEL: f1 = user-data-local
                File[] list1 = f1.listFiles();
                if (list1 != null) {
                    for (File f2 : f1.listFiles()) {
                        // LEVEL: f2 = test-data
                        File[] list2 = f2.listFiles();
                        if (list2 != null) {
                            for (File f3 : list2) {
                                // LEVEL: f3 = data1,...
                                f3.delete();
                            }
                        }
                        f2.delete();
                    }
                }
                f1.delete();
            }
        }
    }

    @Test
    public void constructorRejectsNullStorageService() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new LocalFileStorageService(null);
                });
    }

    @Test
    public void getRootFileWorks() {
        assertEquals(appContext.getFilesDir(), localFileStorageService.getRootFile());
    }

    @Test
    public void uploadRejectsNullStorageService() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    localFileStorageService.upload(new byte[1], null);
                });
    }

    @Test
    public void downloadRejectsNullStorageService() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    localFileStorageService.download(null);
                });
    }

    @Test
    public void uploadDownloadWorks() {
        final byte[] TEST_DATA_1 = "Some data to write!".getBytes(StandardCharsets.UTF_8);
        final String PATH_1 = PATH + "data1";
        localFileStorageService.upload(TEST_DATA_1, PATH_1).join();
        byte[] res = localFileStorageService.download(PATH_1).join();
        assertEquals(
                new String(TEST_DATA_1, StandardCharsets.UTF_8),
                new String(res, StandardCharsets.UTF_8));
    }

    @Test
    public void deleteWorks() {
        final byte[] TEST_DATA_1 = "Some data to write!".getBytes(StandardCharsets.UTF_8);
        final String PATH_1 = PATH + "data1";
        localFileStorageService.upload(TEST_DATA_1, PATH_1).join();
        localFileStorageService.delete(PATH_1).join();
        byte[] b = localFileStorageService.download(PATH_1).join();
        assertNull(b);
    }

    @Test
    public void listDirWorks() {
        final byte[] TEST_DATA_1 = "Some data to write!".getBytes(StandardCharsets.UTF_8);
        final String PATH_1 = PATH + "data1";
        final byte[] TEST_DATA_2 = "Some data2 to write!".getBytes(StandardCharsets.UTF_8);
        final String PATH_2 = PATH + "data2";
        localFileStorageService.upload(TEST_DATA_1, PATH_1).join();
        localFileStorageService.upload(TEST_DATA_2, PATH_2).join();
        String[] strings = localFileStorageService.listDir(PATH).join();
        assertEquals("data1", strings[0]);
        assertEquals("data2", strings[1]);
    }
}
