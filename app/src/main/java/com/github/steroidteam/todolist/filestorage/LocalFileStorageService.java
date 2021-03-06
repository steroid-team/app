package com.github.steroidteam.todolist.filestorage;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class LocalFileStorageService implements FileStorageService {

    private final File localPath;
    private final FirebaseUser user;
    private final String USER_SPACE;

    public LocalFileStorageService(@NonNull File localPath, FirebaseUser user) {
        Objects.requireNonNull(localPath);
        this.localPath = localPath;
        this.user = user;
        this.USER_SPACE = "user-data/" + user.getUid() + "/";
    }

    public File getRootFile() {
        return localPath;
    }

    private String getUserspaceRef(@NonNull String path) {
        Objects.requireNonNull(path);
        return USER_SPACE + path;
    }

    public CompletableFuture<Long> getLastModifiedTime(@NonNull String path) {
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();
        File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
        completableFuture.complete(file.lastModified());
        return completableFuture;
    }

    @Override
    public CompletableFuture<String> upload(byte[] bytes, @NonNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    writeOnFile(bytes, file);
                    return path;
                });
    }

    @Override
    public CompletableFuture<String> upload(
            InputStream inputStream, @NonNull @NotNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    writeOnFile(inputStream, file);
                    return path;
                });
    }

    @Override
    public CompletableFuture<byte[]> downloadBytes(@NonNull @NotNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    return readOnFile(file);
                });
    }

    @Override
    public CompletableFuture<File> downloadFile(
            @NonNull @NotNull String path, @NonNull @NotNull String destinationPath) {

        /*
         * WARNING!
         *
         * As you are managing file locally there are already present so we just return the destinationPath.
         * If they are not you should download it from the remote database.
         */

        CompletableFuture<File> fileCompletableFuture = new CompletableFuture<>();
        File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
        fileCompletableFuture.complete(file);
        return fileCompletableFuture;
    }

    @Override
    public CompletableFuture<Void> delete(@NonNull String path) {
        Objects.requireNonNull(path);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        return CompletableFuture.runAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    if (file.exists()) {
                        file.delete();
                    }
                });
    }

    public CompletableFuture<String[]> listDir(@NonNull String path) {
        Objects.requireNonNull(path);
        File file = new File(this.getRootFile(), this.getUserspaceRef(path));

        return CompletableFuture.supplyAsync(
                () -> {
                    File[] fileList = file.listFiles();
                    if (fileList != null) {
                        return Arrays.stream(fileList).map(File::getName).toArray(String[]::new);
                    } else {
                        return new String[0];
                    }
                });
    }

    // PRIVATE HELPER PART
    private File getFile(File rootDestination, String path) {
        return new File(rootDestination, path);
    }

    private byte[] readOnFile(File file) {
        byte[] data = null;
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
            } catch (IOException e) {
                Log.e("ERROR", "CAN4T READ FILE");
            }
        }
        return data;
    }

    private void writeOnFile(byte[] data, File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (Exception ignored) {
        }
    }

    private void writeOnFile(InputStream data, File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesLen;
            while ((bytesLen = data.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesLen);
            }
            fos.flush();
            fos.close();
        } catch (Exception ignored) {
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
