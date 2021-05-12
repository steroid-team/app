package com.github.steroidteam.todolist.model.todo;

import android.graphics.Color;
import java.util.HashSet;
import java.util.UUID;

public class Tag {
    UUID uuid;
    private String body;
    private int color;
    private HashSet<TodoList> lists = new HashSet<>();

    public Tag(String body, int color) {
        this.body = body;
        this.color = color;
        this.uuid = UUID.randomUUID();
    }

    public Tag(String body) {
        this(body, Color.WHITE);
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

    public void addList(TodoList list) {
        lists.add(list);
    }

    public void removeList(TodoList list) {
        lists.remove(list);
    }

    @Override
    public String toString() {
        return ("Tag{" + body + "}");
    }
}
