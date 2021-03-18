package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;
import com.github.steroidteam.todolist.util.JSONSerializer;
import com.google.gson.JsonSyntaxException;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class JSONSerializerTest {
    @Test
    public void todoListSerializerRejectsNull() {
        assertThrows(NullPointerException.class, () -> {
            JSONSerializer.serializeTodoList(null);
        });
    }

    @Test
    public void todoListDeserializerRejectsNull() {
        assertThrows(NullPointerException.class, () -> {
            JSONSerializer.deserializeTodoList(null);
        });
    }

    @Test
    public void todoListSerializerWoksWithEmptyTasks() {
        final String LIST_TITLE = "Test list";

        final TodoList todoList = new TodoList(LIST_TITLE);
        final String expectedFormattedDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL).format(todoList.getDate());

        final String actualSerializedObject = JSONSerializer.serializeTodoList(todoList);
        final String expectedSerializedObject = "{" +
                "\"id\":\"" + todoList.getId().toString() + "\"," +
                "\"list\":[]," +
                "\"title\":\"" + LIST_TITLE + "\"," +
                "\"date\":\"" + expectedFormattedDate + "\"" +
                "}";
        assertEquals(expectedSerializedObject, actualSerializedObject);
    }

    @Test
    public void todoListSerializerWoksWithNonEmptyTasks() {
        final String LIST_TITLE = "Test list";
        final String TASK1_DESC = "Buy bananas";
        final String TASK2_DESC = "Eat fruit";

        final TodoList todoList = new TodoList(LIST_TITLE);
        todoList.addTask(new Task(TASK1_DESC));
        todoList.addTask(new Task(TASK2_DESC));
        final String expectedFormattedDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL).format(todoList.getDate());

        final String actualSerializedObject = JSONSerializer.serializeTodoList(todoList);
        final String expectedSerializedObject = "{" +
                "\"id\":\"" + todoList.getId().toString() + "\"," +
                "\"list\":[{\"body\":\"" + TASK1_DESC + "\"},{\"body\":\"" + TASK2_DESC + "\"}]," +
                "\"title\":\"" + LIST_TITLE + "\"," +
                "\"date\":\"" + expectedFormattedDate + "\"" +
                "}";
        assertEquals(expectedSerializedObject, actualSerializedObject);
    }

    @Test
    public void todoListDeserializerWorksWithEmptyTasks() {
        final UUID FIXTURE_UUID = UUID.randomUUID();
        final String FIXTURE_TITLE = "Test list";
        final DateFormat FIXTURE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL);
        final String FIXTURE_FORMATTED_DATE = FIXTURE_DATE_FORMAT.format(new Date());

        final String FIXTURE_JSON = "{" +
                "\"id\":\"" + FIXTURE_UUID.toString() + "\"," +
                "\"list\":[]," +
                "\"title\":\"" + FIXTURE_TITLE + "\"," +
                "\"date\":\"" + FIXTURE_FORMATTED_DATE + "\"" +
                "}";
        final TodoList actualDeserializedObject = JSONSerializer.deserializeTodoList(FIXTURE_JSON);

        assertEquals(actualDeserializedObject.getId(), FIXTURE_UUID);
        assertEquals(actualDeserializedObject.getSize(), 0);
        assertEquals(actualDeserializedObject.getTitle(), FIXTURE_TITLE);
        assertEquals(FIXTURE_DATE_FORMAT.format(actualDeserializedObject.getDate()), FIXTURE_FORMATTED_DATE);
    }

    @Test
    public void todoListDeserializerWorksWithNonEmptyTasks() {
        final UUID FIXTURE_UUID = UUID.randomUUID();
        final String FIXTURE_TITLE = "Test list";
        final String TASK1_DESC = "Buy bananas";
        final String TASK2_DESC = "Eat fruit";
        final DateFormat FIXTURE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL);
        final String FIXTURE_FORMATTED_DATE = FIXTURE_DATE_FORMAT.format(new Date());

        final String FIXTURE_JSON = "{" +
                "\"id\":\"" + FIXTURE_UUID.toString() + "\"," +
                "\"list\":[{\"body\":\"" + TASK1_DESC + "\"},{\"body\":\"" + TASK2_DESC + "\"}]," +
                "\"title\":\"" + FIXTURE_TITLE + "\"," +
                "\"date\":\"" + FIXTURE_FORMATTED_DATE + "\"" +
                "}";
        final TodoList actualDeserializedObject = JSONSerializer.deserializeTodoList(FIXTURE_JSON);

        assertEquals(actualDeserializedObject.getId(), FIXTURE_UUID);
        assertEquals(actualDeserializedObject.getSize(), 2);
        assertEquals(actualDeserializedObject.getTask(0), new Task(TASK1_DESC));
        assertEquals(actualDeserializedObject.getTask(1), new Task(TASK2_DESC));
        assertEquals(actualDeserializedObject.getTitle(), FIXTURE_TITLE);
        assertEquals(FIXTURE_DATE_FORMAT.format(actualDeserializedObject.getDate()), FIXTURE_FORMATTED_DATE);
    }

    @Test
    public void todoListDeserializerThrowsOnMalformedJSON() {
        final String FIXTURE_JSON = "{\"id\":\"123";

        assertThrows(JsonSyntaxException.class, () -> {
            JSONSerializer.deserializeTodoList(FIXTURE_JSON);
        });
    }
}
