package com.github.steroidteam.todolist.database;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.model.notes.Note;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NoteRepository {

    private final Database localDatabase;
    private final Database remoteDatabase;

    private final MutableLiveData<ArrayList<Note>> allNoteLiveData;
    private final MutableLiveData<Note> observedNote;

    public NoteRepository(Context context) {
        this.localDatabase = DatabaseFactory.getLocalDb(context.getCacheDir());
        this.remoteDatabase = DatabaseFactory.getRemoteDb();

        allNoteLiveData = new MutableLiveData<>();
        observedNote = new MutableLiveData<>();

        fetchData();
    }

    public void selectNote(UUID id) {
        this.localDatabase.getNote(id).thenAccept(this.observedNote::postValue);
    }

    public LiveData<ArrayList<Note>> getAllNote() {
        return this.allNoteLiveData;
    }

    public LiveData<Note> getNote() {
        return this.observedNote;
    }

    private void fetchData() {

        CompletableFuture<List<UUID>> localNoteList = this.localDatabase.getNotesList();
        // Recover first local data:
        localNoteList.thenAccept(
                noteArrayList -> {
                    setAllNoteLiveData(noteArrayList, this.localDatabase);
                });

        // Then sync local data with remote database:
        syncData();
    }

    private void syncData() {
        this.remoteDatabase
                .getNotesList()
                .thenCombine(
                        this.localDatabase.getNotesList(),
                        (remoteNoteList, localNoteList) -> {
                            List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();

                            // CHECK THAT ALL REMOTE NOTE WILL BE IN THE LOCAL DATABASE
                            for (UUID noteID : remoteNoteList) {
                                if (localNoteList.contains(noteID)) {
                                    // The note is present in the local and remote file
                                    // system.
                                    // We need to check which copy to keep:
                                    checkLastModified(noteID);
                                } else {
                                    // The note is not present in the local file system.
                                    // We need to add it:
                                    CompletableFuture<Void> future =
                                            this.remoteDatabase
                                                    .getNote(noteID)
                                                    .thenAccept(
                                                            note -> {
                                                                this.localDatabase.putNote(
                                                                        noteID, note);
                                                            });
                                    completableFutureList.add(future);
                                }
                            }

                            // REMOVE ALL TO-DO THAT ARE NOT IN THE REMOTE DATABASE
                            for (UUID noteID : localNoteList) {
                                if (!remoteNoteList.contains(noteID)) {
                                    CompletableFuture<Void> future2 =
                                            this.localDatabase.removeNote(noteID);
                                    completableFutureList.add(future2);
                                }
                            }

                            CompletableFuture<Void> futureOfList =
                                    CompletableFuture.allOf(
                                            completableFutureList.toArray(
                                                    new CompletableFuture[0]));

                            futureOfList
                                    .thenCompose(str -> this.localDatabase.getNotesList())
                                    .thenAccept(
                                            uuids -> {
                                                setAllNoteLiveData(uuids, localDatabase);
                                            });
                            return null;
                        })
                .thenCompose(str -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });
    }

    private void checkLastModified(UUID id) {
        remoteDatabase
                .getLastModifiedTimeNote(id)
                .thenCombine(
                        localDatabase.getLastModifiedTimeNote(id),
                        (remoteTime, localTime) -> {
                            if (remoteTime > localTime) {
                                // The remote copy has been updated more recently than the local
                                // one.
                                // So we stored the remote copy in the local database:
                                this.remoteDatabase
                                        .getNote(id)
                                        .thenAccept(
                                                note -> {
                                                    this.localDatabase.putNote(id, note);
                                                });
                            } else {
                                // The local copy has been updated more recently than the remote
                                // one.
                                // So we stored the local copy in the remote database:
                                this.localDatabase
                                        .getNote(id)
                                        .thenAccept(
                                                note -> {
                                                    this.remoteDatabase.putNote(id, note);
                                                });
                            }
                            return null;
                        });
    }

    private void setAllNoteLiveData(List<UUID> noteIDList, Database database) {
        ArrayList<Note> privateArrayList = new ArrayList<>();
        if (noteIDList.isEmpty()) {
            this.allNoteLiveData.postValue(privateArrayList);
        } else {
            for (UUID noteID : noteIDList) {
                database.getNote(noteID)
                        .thenAccept(
                                note -> {
                                    privateArrayList.add(note);
                                    this.allNoteLiveData.postValue(privateArrayList);
                                });
            }
        }
    }

    public void removeNote(UUID id) {
        this.localDatabase
                .removeNote(id)
                .thenCompose(str -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });

        this.remoteDatabase.removeNote(id);
    }

    public void putNote(Note note) {
        this.localDatabase
                .putNote(note.getId(), note)
                .thenCompose(str -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });

        this.remoteDatabase.putNote(note.getId(), note);
    }

    public void updateNote(UUID noteID, Note updatedNote) {
        this.localDatabase
                .updateNote(noteID, updatedNote)
                .thenCompose(str -> this.localDatabase.getNote(noteID))
                .thenAccept(this.observedNote::postValue)
                .thenCompose(str -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });

        this.remoteDatabase.updateNote(noteID, updatedNote);
    }

    public void setHeaderNote(UUID noteID, String imageFileName) throws FileNotFoundException {

        UUID newUUID = UUID.randomUUID();

        this.localDatabase
                .setHeaderNote(noteID, imageFileName, newUUID)
                .thenCompose(note -> this.localDatabase.getNote(noteID))
                .thenAccept(this.observedNote::postValue);

        this.remoteDatabase.setHeaderNote(noteID, imageFileName, newUUID);
    }

    public CompletableFuture<File> getNoteHeader(UUID imageID, String destinationPath) {
        File file = new File(destinationPath, imageID.toString() + ".jpeg");
        if (file.exists()) {
            return this.localDatabase.getImage(imageID, file.getAbsolutePath());
        } else {
            return this.remoteDatabase.getImage(imageID, file.getAbsolutePath());
        }
    }

    public void removeImage(UUID imageID) {
        this.localDatabase.removeImage(imageID);
        this.remoteDatabase.removeImage(imageID);
    }
}
