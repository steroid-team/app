package com.github.steroidteam.todolist.database;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.model.notes.Note;
import com.google.android.gms.maps.model.LatLng;

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
                                    this.remoteDatabase
                                            .getNote(noteID)
                                            .thenAccept(
                                                    note -> {
                                                        this.localDatabase.putNote(noteID, note);
                                                    });
                                }
                            }

                            // REMOVE ALL TO-DO THAT ARE NOT IN THE REMOTE DATABASE
                            for (UUID noteID : localNoteList) {
                                if (!remoteNoteList.contains(noteID)) {
                                    this.localDatabase.removeNote(noteID);
                                }
                            }
                            return null;
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
                .thenCompose(str -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });

        this.remoteDatabase.updateNote(noteID, updatedNote);
    }

    public void setHeaderNote(UUID noteID, String imageFileName) throws FileNotFoundException {
        this.localDatabase
                .setHeaderNote(noteID, imageFileName)
                .thenCompose(note -> this.localDatabase.getNotesList())
                .thenAccept(
                        uuids -> {
                            setAllNoteLiveData(uuids, localDatabase);
                        });

        this.remoteDatabase.setHeaderNote(noteID, imageFileName);
    }

    public CompletableFuture<File> getNoteHeader(UUID imageID, String destinationPath) {
        /*
        File file = new File(destinationPath);
        if(file.exists())
            return this.localDatabase.getImage(imageID, destinationPath);
        else
            this.localDatabase.getImage(imageID, destinationPath);
         */
            return this.remoteDatabase.getImage(imageID, destinationPath);
    }
}
