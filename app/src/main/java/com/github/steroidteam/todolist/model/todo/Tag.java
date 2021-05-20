package com.github.steroidteam.todolist.model.todo;

import com.github.steroidteam.todolist.R;
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
            new Comparator<Tag>() {
                @Override
                public int compare(Tag t1, Tag t2) {
                    return t1.body.compareTo(t2.body);
                }
            };
    private int[] colors = {
        R.color.tag_1, R.color.tag_2, R.color.tag_3, R.color.tag_4, R.color.tag_5, R.color.tag_6
    };

    public Tag(String body) {
        uuid = UUID.randomUUID();
        this.body = body;
        Random rand = new Random();
        color = colors[rand.nextInt(6)];
        // color = colors.get(0);
        System.out.println("new color : " + color);
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
