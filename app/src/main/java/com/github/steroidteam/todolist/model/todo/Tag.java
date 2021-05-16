package com.github.steroidteam.todolist.model.todo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A class that represents a tag. Tags contain a body and a color. They are used to classify to-do
 * lists in different categories.
 */
public class Tag {
    UUID uuid;
    private String body;
    private int color;
    // TODO : Move to database when adding persistence
    public static Set<Tag> tags = new HashSet<>();

    public Tag(String body, int color) {
        this.uuid = UUID.randomUUID();
        this.body = body;
        this.color = color;
        tags.add(this);
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

    @Override
    public String toString() {
        return ("Tag{" + body + "}");
    }
}
