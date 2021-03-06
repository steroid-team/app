package com.github.steroidteam.todolist.database;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TodoListRepository {

    private final Database localDatabase;
    private final Database remoteDatabase;

    private final MutableLiveData<ArrayList<TodoList>> allTodoLiveData;
    private final MutableLiveData<TodoList> observedTodoList;
    private final MutableLiveData<List<Tag>> localTags;
    private final MutableLiveData<List<Tag>> unlinkedTags;
    private final MutableLiveData<List<Tag>> globalTags;

    public TodoListRepository(Context context) {
        this.localDatabase = DatabaseFactory.getLocalDb(context.getCacheDir());
        this.remoteDatabase = DatabaseFactory.getRemoteDb();

        allTodoLiveData = new MutableLiveData<>();
        observedTodoList = new MutableLiveData<>();

        localTags = new MutableLiveData<>();
        unlinkedTags = new MutableLiveData<>();
        globalTags = new MutableLiveData<>();

        fetchData();
    }

    public void selectTodolist(UUID id) {
        localDatabase.getTodoList(id).thenAccept(this.observedTodoList::postValue);
        setTagsLists(id);
    }

    public LiveData<ArrayList<TodoList>> getAllTodo() {
        return this.allTodoLiveData;
    }

    public LiveData<TodoList> getTodoList() {
        if (this.observedTodoList.getValue() != null) {
            this.observedTodoList.getValue().sortByDate().sortByDone();
        }
        return this.observedTodoList;
    }

    private void fetchData() {

        CompletableFuture<TodoListCollection> localTodoCollection =
                this.localDatabase.getTodoListCollection();
        // Recover first local data:
        localTodoCollection.thenAccept(
                todoListCollection -> {
                    setTodoListMutableLiveData(todoListCollection, this.localDatabase);
                });

        // Then sync local data with remote database:
        syncData();
    }

    private void syncData() {
        this.remoteDatabase
                .getTodoListCollection()
                .thenCombine(
                        this.localDatabase.getTodoListCollection(),
                        (remoteTodoCollection, localTodoCollection) -> {
                            List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();

                            // CHECK THAT ALL REMOTE TO-DO WILL BE IN THE LOCAL DATABASE
                            for (int i = 0; i < remoteTodoCollection.getSize(); ++i) {
                                UUID currentRemoteID = remoteTodoCollection.getUUID(i);
                                if (localTodoCollection.contains(currentRemoteID)) {
                                    // The to-do list is present in the local and remote file
                                    // system.
                                    // We need to check which copy to keep:
                                    checkLastModified(currentRemoteID);
                                } else {
                                    // The to-do list is not present in the local file system.
                                    // We need to add it:
                                    CompletableFuture<Void> future =
                                            this.remoteDatabase
                                                    .getTodoList(currentRemoteID)
                                                    .thenAccept(this.localDatabase::putTodoList);
                                    completableFutureList.add(future);
                                }
                            }

                            // REMOVE ALL TO-DO THAT ARE NOT IN THE REMOTE DATABASE
                            for (int i = 0; i < localTodoCollection.getSize(); ++i) {
                                UUID currentLocalID = localTodoCollection.getUUID(i);
                                if (!remoteTodoCollection.contains(currentLocalID)) {
                                    CompletableFuture<Void> future2 =
                                            this.localDatabase.removeTodoList(currentLocalID);
                                    completableFutureList.add(future2);
                                }
                            }

                            CompletableFuture<Void> futureOfList =
                                    CompletableFuture.allOf(
                                            completableFutureList.toArray(
                                                    new CompletableFuture[0]));

                            futureOfList
                                    .thenCompose(str -> this.localDatabase.getTodoListCollection())
                                    .thenAccept(
                                            todoListCollection -> {
                                                setTodoListMutableLiveData(
                                                        todoListCollection, localDatabase);
                                            });
                            return null;
                        })
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(
                        todoListCollection -> {
                            setTodoListMutableLiveData(todoListCollection, localDatabase);
                        });
    }

    private void checkLastModified(UUID id) {
        remoteDatabase
                .getLastModifiedTimeTodo(id)
                .thenCombine(
                        localDatabase.getLastModifiedTimeTodo(id),
                        (remoteTime, localTime) -> {
                            if (remoteTime > localTime) {
                                // The remote copy has been updated more recently than the local
                                // one.
                                // So we stored the remote copy in the local database:
                                this.remoteDatabase
                                        .getTodoList(id)
                                        .thenAccept(this.localDatabase::putTodoList);
                            } else {
                                // The local copy has been updated more recently than the remote
                                // one.
                                // So we stored the local copy in the remote database:
                                this.localDatabase
                                        .getTodoList(id)
                                        .thenAccept(this.remoteDatabase::putTodoList);
                            }
                            return null;
                        });
    }

    private void setTodoListMutableLiveData(
            TodoListCollection todoListCollection, Database database) {
        ArrayList<TodoList> privateArrayList = new ArrayList<>();
        if (todoListCollection.getSize() == 0) {
            allTodoLiveData.postValue(privateArrayList);
        } else {
            for (int i = 0; i < todoListCollection.getSize(); i++) {
                database.getTodoList(todoListCollection.getUUID(i))
                        .thenAccept(
                                todoList -> {
                                    privateArrayList.add(todoList);
                                    allTodoLiveData.postValue(privateArrayList);
                                });
            }
        }
    }

    public void putTodo(TodoList todoList) {
        this.localDatabase
                .putTodoList(todoList)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(
                        todoListCollection -> {
                            setTodoListMutableLiveData(todoListCollection, localDatabase);
                        });

        this.remoteDatabase.putTodoList(todoList);
    }

    public void removeTodo(UUID id) {
        this.localDatabase
                .removeTodoList(id)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(
                        todoListCollection -> {
                            setTodoListMutableLiveData(todoListCollection, localDatabase);
                        });

        this.remoteDatabase.removeTodoList(id);
    }

    public void updateTodo(UUID id, TodoList todoListUpdated) {
        this.localDatabase
                .updateTodoList(id, todoListUpdated)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(
                        todoListCollection -> {
                            setTodoListMutableLiveData(todoListCollection, localDatabase);
                        });

        this.remoteDatabase.updateTodoList(id, todoListUpdated);
    }

    public void putTask(UUID todoListID, Task task) {
        this.localDatabase
                .putTask(todoListID, task)
                .thenCompose(str -> this.localDatabase.getTodoList(todoListID))
                .thenApply(todoList -> todoList.sortByDate())
                .thenAccept(this.observedTodoList::postValue);
    }

    public void removeTask(UUID todoListID, int index) {
        this.localDatabase
                .removeTask(todoListID, index)
                .thenCompose(str -> this.localDatabase.getTodoList(todoListID))
                .thenApply(todoList -> todoList.sortByDate())
                .thenAccept(this.observedTodoList::postValue);
    }

    public void removeDoneTasks(UUID todoListID) {
        this.localDatabase
                .removeDoneTasks(todoListID)
                .thenCompose(str -> this.localDatabase.getTodoList(todoListID))
                .thenApply(todoList -> todoList.sortByDate())
                .thenAccept(this.observedTodoList::postValue);
    }

    public void updateTask(UUID todoListID, int index, Task updatedTask) {
        this.localDatabase
                .updateTask(todoListID, index, updatedTask)
                .thenCompose(task -> this.localDatabase.getTodoList(todoListID))
                .thenApply(todoList -> todoList.sortByDate())
                .thenAccept(this.observedTodoList::postValue);
    }

    public LiveData<List<Tag>> getLocalTags(UUID todoListID) {
        setTagsLists(todoListID);
        return localTags;
    }

    public LiveData<List<Tag>> getGlobalTags() {
        localDatabase.getAllTags().thenAccept(tags -> globalTags.postValue(tags));
        return globalTags;
    }

    public LiveData<List<Tag>> getUnlinkedTags(UUID todoListID) {
        setTagsLists(todoListID);
        return unlinkedTags;
    }

    public void putTagInTodolist(UUID todoListID, UUID tagId) {
        localDatabase
                .putTagInList(todoListID, tagId)
                .thenCompose(str -> localDatabase.getTodoList(todoListID))
                .thenApply(TodoList::sortByDate)
                .thenAccept(observedTodoList::postValue)
                .thenCompose(str -> localDatabase.getTagsFromList(todoListID))
                .thenAccept(tags -> localTags.postValue(tags))
                .thenAccept(
                        str -> {
                            List<Tag> unlinked = globalTags.getValue();
                            unlinked.removeAll(globalTags.getValue());
                            unlinkedTags.postValue(unlinked);
                        });
    }

    public void putTag(Tag tag) {
        localDatabase
                .putTag(tag)
                .thenCompose(str -> localDatabase.getAllTags())
                .thenAccept(globalTags::postValue)
                .thenAccept(
                        str -> {
                            List<Tag> unlinked = globalTags.getValue();
                            unlinked.removeAll(localTags.getValue());
                            unlinkedTags.postValue(unlinked);
                        });

        remoteDatabase.putTag(tag);
    }

    public void removeTagFromTodolist(UUID todoListID, UUID tagId) {
        localDatabase
                .removeTagFromList(todoListID, tagId)
                .thenCompose(s -> localDatabase.getTodoList(todoListID))
                .thenApply(TodoList::sortByDate)
                .thenAccept(this.observedTodoList::postValue)
                .thenCompose(str -> localDatabase.getTagsFromList(todoListID))
                .thenAccept(tags -> localTags.postValue(tags))
                .thenAccept(
                        str -> {
                            List<Tag> unlinked = globalTags.getValue();
                            unlinked.removeAll(globalTags.getValue());
                            unlinkedTags.postValue(unlinked);
                        });
    }

    public void destroyTag(Tag tag) {
        localDatabase
                .removeTag(tag.getId())
                .thenCompose(str -> localDatabase.getAllTags())
                .thenAccept(globalTags::setValue)
                .thenCompose(str -> localDatabase.getTodoList(observedTodoList.getValue().getId()))
                .thenApply(TodoList::sortByDate)
                .thenAccept(this.observedTodoList::postValue)
                .thenCompose(str -> localDatabase.getAllTags())
                .thenAccept(globalTags::postValue)
                .thenAccept(
                        str -> {
                            List<Tag> unlinked = globalTags.getValue();
                            unlinked.removeAll(globalTags.getValue());
                            unlinkedTags.postValue(unlinked);
                        });
    }

    private void setTagsLists(UUID todoListID) {
        localDatabase
                .getTagsFromList(todoListID)
                .thenAccept(tags -> localTags.postValue(tags))
                .thenCompose(str -> localDatabase.getAllTags())
                .thenAccept(allTags -> globalTags.postValue(allTags))
                .thenAccept(
                        str -> {
                            List<Tag> unlinked = globalTags.getValue();
                            unlinked.removeAll(globalTags.getValue());
                            unlinkedTags.postValue(unlinked);
                        });
    }

    public void setTaskLocationReminder(
            UUID todoListID, int index, LatLng location, String locationName) {
        this.localDatabase
                .getTask(todoListID, index)
                .thenCompose(
                        task -> {
                            task.setRemindAtLocation(location, locationName);
                            return this.localDatabase.updateTask(todoListID, index, task);
                        })
                .thenCompose(task -> this.localDatabase.getTodoList(todoListID))
                .thenAccept(this.observedTodoList::setValue);
        this.localDatabase.getTodoList(todoListID).thenAccept(this.observedTodoList::setValue);
    }
}
