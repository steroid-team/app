package com.github.steroidteam.todolist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
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
    public void getAndSetLatlngWorks() {
        LatLng location = new LatLng(-33.8523341, 151.2106085);

        Note note = new Note("Title");
        assertNull(note.getLatLng());
        assertEquals(note, note.setLatLng(location));
        assertEquals(location, note.getLatLng());
    }

    @Test
    public void getAndSetLocationWorks() {
        String location = "LAUSANNE";

        Note note = new Note("Title");
        assertNull(note.getLocationName());
        assertEquals(note, note.setLocationName(location));
        assertEquals(location, note.getLocationName());
    }
}
