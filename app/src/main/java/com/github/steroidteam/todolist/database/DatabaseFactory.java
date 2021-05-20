package com.github.steroidteam.todolist.database;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.VisibleForTesting;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.storage.FirebaseStorage;
import java.io.File;

public final class DatabaseFactory {

    private static Database customLocalDb = null;
    private static Database customRemoteDb = null;

    private DatabaseFactory() {}

    public static Database getLocalDb(File localDir) {
        if (customLocalDb == null) {
            return new FileStorageDatabase(
                    new LocalFileStorageService(localDir, UserFactory.get()));
        } else {
            return customLocalDb;
        }
    }

    public static Database getRemoteDb() {
        if (customRemoteDb == null) {
            return new FileStorageDatabase(
                    new FirebaseFileStorageService(
                            FirebaseStorage.getInstance(), UserFactory.get()));
        } else {
            return customRemoteDb;
        }
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public static void setCustomDatabase(Database db) {
        DatabaseFactory.customLocalDb = db;
        DatabaseFactory.customRemoteDb = db;
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public static void setCustomDatabaseDiff(Database customLocalDb, Database customRemoteDb) {
        DatabaseFactory.customLocalDb = customLocalDb;
        DatabaseFactory.customRemoteDb = customRemoteDb;
    }
}
