package com.github.steroidteam.todolist.filestorage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.steroidteam.todolist.user.FBaseUser;
import com.github.steroidteam.todolist.user.User;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LocalFileStorageService implements FileStorageService {

    private final File localPath;
    private final FirebaseUser user;
    private final String USER_SPACE;

    public LocalFileStorageService(@NonNull File localPath, @NonNull FirebaseUser user) {
        Objects.requireNonNull(localPath);
        Objects.requireNonNull(user);
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

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        return CompletableFuture.runAsync(
                () -> {
                    File file = getFile(this.getRootFile(), this.getUserspaceRef(path));
                    System.err.println("DELETE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    if (file.exists()) {
                        System.err.println("FILE EXISTS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        boolean tmp = file.delete();
                        System.err.println(tmp + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
        System.err.println("data = " + data.toString());
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
