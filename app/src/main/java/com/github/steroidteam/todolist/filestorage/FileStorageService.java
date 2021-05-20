package com.github.steroidteam.todolist.filestorage;

import androidx.annotation.NonNull;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface FileStorageService {
    /**
     * Create a file in the filesystem with the specified binary contents.
     *
     * @param bytes The binary content that should be written in the file.
     * @param path The path where the file should be created, in the format and structure used by
     *     the specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns the new file's path upon completion.
     */
    CompletableFuture<String> upload(byte[] bytes, @NonNull String path);

    /**
     * Create a file in the filesystem with the specified binary contents.
     *
     * @param inputStream The stream from which its content is written in the file.
     * @param path The path where the file should be created, in the format and structure used by
     *     the specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns the new file's path upon completion.
     */
    CompletableFuture<String> upload(InputStream inputStream, @NonNull String path);

    /**
     * Fetch a file from the filesystem and stores it locally as a byte array.
     *
     * @param path The path to the file to fetch, in the format and structure used by the specific
     *     filesystem. Cannot be null.
     * @return A CompletableFuture that returns the file's binary contents upon completion.
     */
    CompletableFuture<byte[]> downloadBytes(@NonNull String path);

    /**
     * Fetch a file from the filesystem and stores it locally as a file.
     *
     * @param path The path to the file to fetch, in the format and structure used by the specific
     *     filesystem. Cannot be null.
     * @param destinationPath The path in the local filesystem where the downloaded file should be
     *     stored upon completion.
     * @return A CompletableFuture that returns the file upon completion.
     */
    CompletableFuture<File> downloadFile(@NonNull String path, @NonNull String destinationPath);

    /**
     * Delete a file from the filesystem.
     *
     * @param path The path to the file to delete, in the format and structure used by the specific
     *     filesystem. Cannot be null.
     * @return A CompletableFuture that returns nothing upon completion.
     */
    CompletableFuture<Void> delete(@NonNull String path);

    /**
     * List all the files in a directory.
     *
     * @param path The path to the directory to list, in the format and structure used by the
     *     specific filesystem. Cannot be null.
     * @return A CompletableFuture that returns an array of strings, each with the path of the files
     *     in the directory.
     */
    CompletableFuture<String[]> listDir(@NonNull String path);

    CompletableFuture<Long> getLastModifiedTime(@NonNull String path);
}
