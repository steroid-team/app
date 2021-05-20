package com.github.steroidteam.todolist.model.todo;

import android.graphics.Color;
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
        Color.rgb(255, 154, 162), Color.rgb(255, 183, 178),
        Color.rgb(255, 218, 193), Color.rgb(226, 240, 203),
        Color.rgb(181, 234, 215), Color.rgb(199, 206, 234)
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
}
