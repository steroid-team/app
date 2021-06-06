package com.github.steroidteam.todolist.model.todo;

import java.util.Comparator;
import java.util.Random;
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
            (t1, t2) -> t1.body.toLowerCase().compareTo(t2.body.toLowerCase());
    private static final int[] TAG_COLORS = {
        0xffff9aa2, 0xffffb7b2, 0xffffdac1, 0xffe2f0cb, 0xffb5ead7, 0xffc7ceea
    };

    public Tag(String body) {
        uuid = UUID.randomUUID();
        this.body = body;
        Random rand = new Random();
        color = TAG_COLORS[rand.nextInt(6)];
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

    @Override
    public boolean equals(Object otherTag) {
        return (otherTag instanceof Tag && ((Tag) otherTag).getId().equals(uuid));
    }
}
