package com.github.steroidteam.todolist.model.todo;

import android.graphics.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A class that represents a tag. Tags contain a body and a color. They are used to classify to-do
 * lists in different categories.
 */
public class Tag {
    UUID uuid;
    private String body;
    private int color;
    private Set<UUID> listsIds;
    // TODO : Move to database when adding persistence
    public static Set<Tag> tags = new HashSet<>();

    public Tag(String body, int color, UUID listId) {
        this.uuid = UUID.randomUUID();
        this.body = body;
        this.color = color;
        listsIds = new HashSet<>();
        listsIds.add(listId);
        tags.add(this);
    }

    public Tag(String body, UUID listId) {
        this(body, Color.WHITE, listId);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int newColor) {
        color = newColor;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String newBody) {
        body = newBody;
    }

    public Set<UUID> getListsIds() {
        return listsIds;
    }

    public void addList(UUID listId) {
        listsIds.add(listId);
    }

    public void removeList(UUID listId) {
        listsIds.remove(listId);
    }

    public static Set<Tag> getTagsFromListId(UUID listId) {
        return tags.stream()
                .filter(tag -> tag.getListsIds().contains(listId))
                .collect(Collectors.toSet());
    }

    public boolean containsId(UUID listId) {
        return listsIds.contains(listId);
    }

    @Override
    public String toString() {
        return ("Tag{" + body + "}");
    }
}
