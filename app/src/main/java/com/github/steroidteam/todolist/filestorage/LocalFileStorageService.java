package com.github.steroidteam.todolist.filestorage;

import android.content.Context;
import androidx.annotation.NonNull;

import com.github.steroidteam.todolist.database.DatabaseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LocalFileStorageService implements FileStorageService{

    private final Context context;

    public LocalFileStorageService(Context context) {
        this.context = context;
    }

    private String getUserspaceRef(@NonNull String path) {
        Objects.requireNonNull(path);
        // When we will have a user interface:
        //"user-data/" + this.user.getUid() + "/" + path
        return "user-data/" + path;
    }

    @Override
    public CompletableFuture<String> upload(byte[] bytes, @NonNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(() -> {
            File file = getFile(context.getFilesDir(), this.getUserspaceRef(path));
            try {
                writeOnFile(bytes.toString(), file);
            } catch (DatabaseException ignored) {
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<byte[]> download(@NonNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.supplyAsync(() -> {
            File file = getFile(context.getFilesDir(), this.getUserspaceRef(path));
            try {
                return readOnFile(file).getBytes(StandardCharsets.UTF_8);
            } catch (DatabaseException e) {
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> delete(@NonNull String path) {
        Objects.requireNonNull(path);

        return CompletableFuture.runAsync(() -> {
            File file = getFile(context.getFilesDir(), this.getUserspaceRef(path));
            if (file.exists()) {
                file.delete();
            }
        });
    }

    public CompletableFuture<String[]> listDir(@NonNull String path) {
        Objects.requireNonNull(path);
        CompletableFuture<String[]> completableFuture = new CompletableFuture<>();

        File file = new File(context.getFilesDir(), this.getUserspaceRef(path));

        return CompletableFuture.supplyAsync(() -> {
            return Arrays.stream(file.listFiles()).map(File::getName)
                    .toArray(String[]::new);
        });
    }

    // PRIVATE HELPER PART
    private File getFile(File rootDestination, String path) {
        return new File(rootDestination, path);
    }

    private String readOnFile(File file) throws DatabaseException {
        String result = null;
        if (file.exists()) {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    result = sb.toString();
                }
            } catch (Exception e) {
                throw new DatabaseException("unable to read file at: " + file);
            }
        }
        return result;
    }

    private void writeOnFile(String text, File file) throws DatabaseException {

        try {
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            try (Writer w = new BufferedWriter(new OutputStreamWriter(fos))) {
                w.write(text);
                w.flush();
                fos.getFD().sync();
            }
        } catch (Exception e) {
            throw new DatabaseException("unable to write on file at: " + file);
        }
    }
}
