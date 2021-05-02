package com.github.steroidteam.todolist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import com.github.steroidteam.todolist.model.notes.Note;
import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;

public class NoteTest {

    @Test
    public void NoteCorrectlyThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    new Note(null);
                });
    }

    @Test
    public void NotesGetDifferentUUID() {
        Note note1 = new Note("");
        Note note2 = new Note("");
        assertNotEquals(note1.getId(), note2.getId());
    }

    @Test
    public void getAndSetTitleWorks() {
        String title = "Title";
        String newTitle = "New title";

        Note note = new Note(title);
        assertEquals(title, note.getTitle());
        note.setTitle(newTitle);
        assertEquals(newTitle, note.getTitle());
    }

    @Test
    public void getAndSetContentWorks() {
        String content = "Content";

        Note note = new Note("Title");
        assertEquals("", note.getContent());
        note.setContent(content);
        assertEquals(content, note.getContent());
    }

    @Test
    public void getAndSetLocationWorks() {
        LatLng location = new LatLng(-33.8523341, 151.2106085);

        Note note = new Note("Title");
        assertEquals(null, note.getLatLng());
        note.setLatLng(location);
        assertEquals(location, note.getLatLng());
    }

    @Test
    public void toStringWorks() {
        Note note = new Note("Title");
        note.setContent("body");
        assertEquals(
                "Note{\ntitle='Title',\ncontent='body',\ncoordinates='null'\n}", note.toString());

        LatLng location = new LatLng(-33.8523341, 151.2106085);
        note.setLatLng(location);
        assertEquals(
                "Note{\ntitle='Title',\ncontent='body',\ncoordinates='"
                        + location.toString()
                        + "'\n}",
                note.toString());
    }

    @Test
    public void equalsWorks() {
        Note note1 = new Note("Title");
        Note note2 = new Note("Title");
        Note note3 = note1;
        assertEquals(note1, note3);
        assertNotEquals(note1, note2);
        assertNotEquals(note1, null);
    }
}
