package com.github.steroidteam.todolist.model.todo;

import java.util.Comparator;
import java.util.UUID;

/**
 * A class that represents a tag. Tags contain a body and a color. They are used to classify to-do
 * lists in different categories.
 */
public class Tag {
    private UUID uuid;
    private String body;
    private int color;
    public static Comparator<Tag> sortByBody =
            new Comparator<Tag>() {
                @Override
                public int compare(Tag t1, Tag t2) {
                    return t1.body.compareTo(t2.body);
                }
            };

    public Tag(String body, int color) {
        this.uuid = UUID.randomUUID();
        this.body = body;
        this.color = color;
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

    public UUID getId() {
        return uuid;
    }

    @Override
    public String toString() {
        return ("Tag{" + body + "}");
    }
}
