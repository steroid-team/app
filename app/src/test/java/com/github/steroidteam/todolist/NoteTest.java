package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.notes.Note;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

public class NoteTest {

    @Test
    public void NoteCorrectlyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
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
}
