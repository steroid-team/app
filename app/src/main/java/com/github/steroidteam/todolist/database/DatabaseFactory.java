package com.github.steroidteam.todolist.database;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.VisibleForTesting;
import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.storage.FirebaseStorage;

public final class DatabaseFactory {
    private static Database customDb = null;

    private DatabaseFactory() {}

    public static Database getDb() {
        if (customDb == null) {
            return new FirebaseDatabase(
                    new FirebaseFileStorageService(
                            FirebaseStorage.getInstance(), UserFactory.get()));
        } else {
            return customDb;
        }
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public static void setCustomDatabase(Database db) {
        DatabaseFactory.customDb = db;
    }
}
