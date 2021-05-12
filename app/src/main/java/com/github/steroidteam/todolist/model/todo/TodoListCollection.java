package com.github.steroidteam.todolist.model.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TodoListCollection {
    private final List<UUID> todoListCollection;
    private final List<UUID> tagCollection;

    public TodoListCollection() {
        this.todoListCollection = new ArrayList<>();
        tagCollection = new ArrayList<>();
    }

    public TodoListCollection(List<UUID> todoListCollection) {
        this.todoListCollection = todoListCollection;
        tagCollection = new ArrayList<>();
    }

    public int getSize() {
        return todoListCollection.size();
    }

    public UUID getUUID(int index) {
        return todoListCollection.get(index);
    }

    public void addUUID(UUID uuid) {
        todoListCollection.add(uuid);
    }

    public void removeUUID(UUID uuid) {
        todoListCollection.remove(uuid);
    }
}
