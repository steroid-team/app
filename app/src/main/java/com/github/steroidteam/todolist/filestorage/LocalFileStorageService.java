package com.github.steroidteam.todolist.filestorage;

import android.content.Context;
import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.util.FileStorageUtils;
import java.util.concurrent.CompletableFuture;

public class LocalFileStorageService {

    private final Context context;

    public LocalFileStorageService(Context context) {
        this.context = context;
    }

    public void upload(String bytes, @NonNull String path) {
        FileStorageUtils.setFileInStorage(context.getFilesDir(), path, bytes);
    }

    public String download(@NonNull String path) {
        return FileStorageUtils.getFileFromStorage(context.getFilesDir(), path);
    }

    public void delete(@NonNull String path) {
        FileStorageUtils.deleteFileFromStorage(context.getFilesDir(), path);
    }

    public CompletableFuture<String[]> listDir(@NonNull String path) {
        return null;
    }
}
