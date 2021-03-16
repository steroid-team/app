package com.github.steroidteam.todolist.filestorage;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

public interface FileStorageService {
    /**
     * Create a file in the filesystem with the specified binary contents.
     *
     * @param bytes The binary content that should be written in the file.
     * @param path  The path where the file should be created, in the format and structure used
     *              by the specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns the new file's path upon completion.
     */
    CompletableFuture<String> upload(byte[] bytes, @NonNull String path);

    /**
     * Fetch a file from the filesystem.
     *
     * @param path The path to the file to fetch, in the format and structure used by the
     *             specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns the file's binary contents upon completion.
     */
    CompletableFuture<byte[]> download(@NonNull String path);

    /**
     * Delete a file from the filesystem.
     *
     * @param path The path to the file to delete, in the format and structure used by the
     *             specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns nothing upon completion.
     */
    CompletableFuture<Void> delete(@NonNull String path);

    /**
     * List all the files in a directory.
     *
     * @param path The path to the directory to list, in the format and structure used by the
     *             specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns an array of strings, each with the path of the
     * files in the directory.
     */
    CompletableFuture<String[]> listDir(@NonNull String path);
}
