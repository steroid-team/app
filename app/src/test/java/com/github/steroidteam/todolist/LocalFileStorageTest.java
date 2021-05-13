package com.github.steroidteam.todolist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileStorageTest {

    private final String PATH = "test-data/";
    private LocalFileStorageService localFileStorageService;
    private final File fakeLocalPath = new File("FAKE-LOCAL-PATH/");

    @Mock FirebaseUser user;

    @Before
    public void initFileStorage() {
        String userID = "fake id";
        doReturn(userID).when(user).getUid();
        // Context of the app under test.
        localFileStorageService = new LocalFileStorageService(fakeLocalPath, user);
    }

    @After
    public void removeAllTestFiles() {
        File[] files = fakeLocalPath.listFiles();
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
                    new LocalFileStorageService(null, null);
                });
    }

    @Test
    public void getRootFileWorks() {
        assertEquals(fakeLocalPath, localFileStorageService.getRootFile());
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

    @Test
    public void getLastTimeWorks() {
        final byte[] TEST_DATA_1 = "Some data to write!".getBytes(StandardCharsets.UTF_8);
        final String PATH_1 = PATH + "data1";
        Long justBeforeTime = System.currentTimeMillis();
        localFileStorageService.upload(TEST_DATA_1, PATH_1).join();
        Long justAfterTime = System.currentTimeMillis();
        assertTrue(justBeforeTime <= localFileStorageService.getLastModifiedTime(PATH_1).join());
        assertTrue(justAfterTime >= localFileStorageService.getLastModifiedTime(PATH_1).join());
    }
}
