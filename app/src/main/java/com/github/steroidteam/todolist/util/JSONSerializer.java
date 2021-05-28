package com.github.steroidteam.todolist.util;

import androidx.annotation.NonNull;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Objects;

public class JSONSerializer {
    private static final Gson gson =
            new GsonBuilder()
                    // Date serialization might depend on the system, so let's make it deterministic
                    // across platforms.
                    .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                    .create();

    /**
     * Transforms a TodoList object into a JSON object.
     *
     * @param todoList The object to serialize. Cannot be null.
     * @return A string with the corresponding JSON object.
     */
    public static String serializeTodoList(@NonNull TodoList todoList) {
        Objects.requireNonNull(todoList);
        return JSONSerializer.gson.toJson(todoList);
    }

    /**
     * Transforms a JSON representation of a TodoList into an actual TodoList object.
     *
     * @param serializedTodoList A JSON string with the object to deserialize. Cannot be null.
     * @return A TodoList object that matches the JSON's content.
     */
    public static TodoList deserializeTodoList(@NonNull String serializedTodoList) {
        Objects.requireNonNull(serializedTodoList);
        return JSONSerializer.gson.fromJson(serializedTodoList, TodoList.class);
    }

    /**
     * Transforms a Note object into a JSON object.
     *
     * @param note The object to serialize. Cannot be null.
     * @return A string with the corresponding JSON object.
     */
    public static String serializeNote(@NonNull Note note) {
        Objects.requireNonNull(note);
        return JSONSerializer.gson.toJson(note);
    }

    /**
     * Transforms a JSON representation of a TodoList into an actual TodoList object.
     *
     * @param serializedNote A JSON string with the object to deserialize. Cannot be null.
     * @return A Note object that matches the JSON's content.
     */
    public static Note deserializeNote(@NonNull String serializedNote) {
        Objects.requireNonNull(serializedNote);
        return JSONSerializer.gson.fromJson(serializedNote, Note.class);
    }

    public static String serializeTag(Tag tag) {
        Objects.requireNonNull(tag);
        return JSONSerializer.gson.toJson(tag);
    }

    public static Tag deserializeTag(@NonNull String serializedTag) {
        Objects.requireNonNull(serializedTag);
        return JSONSerializer.gson.fromJson(serializedTag, Tag.class);
    }
}
