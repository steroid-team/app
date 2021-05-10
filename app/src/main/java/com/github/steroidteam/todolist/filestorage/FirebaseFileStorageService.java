package com.github.steroidteam.todolist.filestorage;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FirebaseFileStorageService implements FileStorageService {
    public static final long MAX_DOWNLOAD_BYTES = 1024 * 1024; // 1 MiB

    private final FirebaseUser user;
    private final StorageReference storageRef;

    public FirebaseFileStorageService(
            @NonNull FirebaseStorage storage, @NonNull FirebaseUser user) {
        Objects.requireNonNull(storage);
        Objects.requireNonNull(user);
        this.user = user;
        this.storageRef = storage.getReference();
    }

    private StorageReference getUserspaceRef(@NonNull String path) {
        Objects.requireNonNull(path);
        return storageRef.child("user-data/" + this.user.getUid() + "/" + path);
    }

    public CompletableFuture<Long> getLastModifiedTime(@NonNull String path) {
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();

        this.getUserspaceRef(path)
                .getMetadata()
                .addOnSuccessListener(
                        storageMetadata ->
                                completableFuture.complete(storageMetadata.getUpdatedTimeMillis()))
                .addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }

    public CompletableFuture<String> upload(byte[] bytes, @NonNull String path) {
        Objects.requireNonNull(path);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        this.getUserspaceRef(path)
                .putBytes(bytes)
                .addOnSuccessListener(
                        taskSnapshot -> {
                            completableFuture.complete(taskSnapshot.getMetadata().getPath());
                        })
                .addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }

    public CompletableFuture<byte[]> download(@NonNull String path) {
        Objects.requireNonNull(path);
        CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();

        this.getUserspaceRef(path)
                .getBytes(FirebaseFileStorageService.MAX_DOWNLOAD_BYTES)
                .addOnSuccessListener(completableFuture::complete)
                .addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }

    public CompletableFuture<Void> delete(@NonNull String path) {
        Objects.requireNonNull(path);
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        this.getUserspaceRef(path)
                .delete()
                .addOnSuccessListener(completableFuture::complete)
                .addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }

    public CompletableFuture<String[]> listDir(@NonNull String path) {
        Objects.requireNonNull(path);
        CompletableFuture<String[]> completableFuture = new CompletableFuture<>();

        this.getUserspaceRef(path)
                .listAll()
                .addOnSuccessListener(
                        listResult -> {
                            completableFuture.complete(
                                    listResult.getItems().stream()
                                            .map(StorageReference::getName)
                                            .toArray(String[]::new));
                        })
                .addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }
}
