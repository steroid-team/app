package com.github.steroidteam.todolist.filestorage;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LocalFileStorageService implements FileStorageService {

    private final Context context;
    private final String USER_SPACE = "user-data-local/";

    public LocalFileStorageService(@NonNull Context context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    public File getRootFile() {
        return context.getFilesDir();
    }

    private String getUserspaceRef(@NonNull String path) {
        Objects.requireNonNull(path);
        return USER_SPACE + path;
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
    public CompletableFuture<byte[]> download(@NonNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    return readOnFile(file);
                });
    }

    @Override
    public CompletableFuture<Void> delete(@NonNull String path) {
        Objects.requireNonNull(path);

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
        CompletableFuture<String[]> completableFuture = new CompletableFuture<>();

        File file = new File(this.getRootFile(), this.getUserspaceRef(path));

        return CompletableFuture.supplyAsync(
                () -> {
                    File[] fileList = file.listFiles();
                    if(fileList!=null) {
                        return Arrays.stream(fileList)
                                .map(File::getName)
                                .toArray(String[]::new);
                    }
                    else {
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
                e.printStackTrace();
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
}
