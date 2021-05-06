package com.github.steroidteam.todolist.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.google.gson.JsonSyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.junit.Test;

public class JSONSerializerTest {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Test
    public void todoListSerializerRejectsNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    JSONSerializer.serializeTodoList(null);
                });
    }

    @Test
    public void todoListDeserializerRejectsNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    JSONSerializer.deserializeTodoList(null);
                });
    }

    @Test
    public void todoListSerializerWoksWithEmptyTasks() {
        final String LIST_TITLE = "Test list";

        final TodoList todoList = new TodoList(LIST_TITLE);
        final String expectedFormattedDate = DATE_FORMAT.format(todoList.getDate());

        final String actualSerializedObject = JSONSerializer.serializeTodoList(todoList);
        final String expectedSerializedObject =
                "{"
                        + "\"id\":\""
                        + todoList.getId().toString()
                        + "\","
                        + "\"list\":[],"
                        + "\"title\":\""
                        + LIST_TITLE
                        + "\","
                        + "\"date\":\""
                        + expectedFormattedDate
                        + "\""
                        + "}";
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
        final String expectedFormattedDate = DATE_FORMAT.format(todoList.getDate());

        final String actualSerializedObject = JSONSerializer.serializeTodoList(todoList);
        final String expectedSerializedObject =
                "{"
                        + "\"id\":\""
                        + todoList.getId().toString()
                        + "\","
                        + "\"list\":[{\"body\":\""
                        + TASK1_DESC
                        + "\",\"done\":"
                        + false
                        + "},{\"body\":\""
                        + TASK2_DESC
                        + "\",\"done\":"
                        + false
                        + "}],"
                        + "\"title\":\""
                        + LIST_TITLE
                        + "\","
                        + "\"date\":\""
                        + expectedFormattedDate
                        + "\""
                        + "}";
        assertEquals(expectedSerializedObject, actualSerializedObject);
    }

    @Test
    public void todoListDeserializerWorksWithEmptyTasks() {
        final UUID FIXTURE_UUID = UUID.randomUUID();
        final String FIXTURE_TITLE = "Test list";
        final String FIXTURE_FORMATTED_DATE = DATE_FORMAT.format(new Date());

        final String FIXTURE_JSON =
                "{"
                        + "\"id\":\""
                        + FIXTURE_UUID.toString()
                        + "\","
                        + "\"list\":[],"
                        + "\"title\":\""
                        + FIXTURE_TITLE
                        + "\","
                        + "\"date\":\""
                        + FIXTURE_FORMATTED_DATE
                        + "\""
                        + "}";
        final TodoList actualDeserializedObject = JSONSerializer.deserializeTodoList(FIXTURE_JSON);

        assertEquals(actualDeserializedObject.getId(), FIXTURE_UUID);
        assertEquals(actualDeserializedObject.getSize(), 0);
        assertEquals(actualDeserializedObject.getTitle(), FIXTURE_TITLE);
        assertEquals(
                DATE_FORMAT.format(actualDeserializedObject.getDate()), FIXTURE_FORMATTED_DATE);
    }

    @Test
    public void todoListDeserializerWorksWithNonEmptyTasks() {
        final UUID FIXTURE_UUID = UUID.randomUUID();
        final String FIXTURE_TITLE = "Test list";
        final Task TASK1 = new Task("Buy bananas");
        TASK1.setDone(true);
        final Task TASK2 = new Task("Eat fruit");
        TASK1.setDone(false);
        final String FIXTURE_FORMATTED_DATE = DATE_FORMAT.format(new Date());

        final String FIXTURE_JSON =
                "{"
                        + "\"id\":\""
                        + FIXTURE_UUID.toString()
                        + "\","
                        + "\"list\":["
                        + "{"
                        + ("\"body\":\"" + TASK1.getBody() + "\",")
                        + ("\"done\":" + TASK1.isDone() + "")
                        + "},{"
                        + ("\"body\":\"" + TASK2.getBody() + "\",")
                        + ("\"done\":" + TASK2.isDone() + "")
                        + "\"}],"
                        + "\"title\":\""
                        + FIXTURE_TITLE
                        + "\","
                        + "\"date\":\""
                        + FIXTURE_FORMATTED_DATE
                        + "\""
                        + "}";
        final TodoList actualDeserializedObject = JSONSerializer.deserializeTodoList(FIXTURE_JSON);

        assertEquals(actualDeserializedObject.getId(), FIXTURE_UUID);
        assertEquals(actualDeserializedObject.getSize(), 2);
        assertEquals(actualDeserializedObject.getTask(0), TASK1);
        assertEquals(actualDeserializedObject.getTask(1), TASK2);
        assertEquals(actualDeserializedObject.getTitle(), FIXTURE_TITLE);
        assertEquals(
                DATE_FORMAT.format(actualDeserializedObject.getDate()), FIXTURE_FORMATTED_DATE);
    }

    @Test
    public void todoListDeserializerThrowsOnMalformedJSON() {
        final String FIXTURE_JSON = "{\"id\":\"123";

        assertThrows(
                JsonSyntaxException.class,
                () -> {
                    JSONSerializer.deserializeTodoList(FIXTURE_JSON);
                });
    }
}
