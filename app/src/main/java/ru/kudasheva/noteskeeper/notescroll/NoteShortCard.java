package ru.kudasheva.noteskeeper.notescroll;

import ru.kudasheva.noteskeeper.data.models.Note;

public class NoteShortCard {
    private String id;
    private String header;
    private String date;
    private boolean shared;

    public NoteShortCard(String id, String header, String date, boolean shared) {
        this.id = id;
        this.header = header;
        this.date = date;
        this.shared = shared;
    }

    public NoteShortCard(Note note) {
        this.id = note.get_id();
        this.header = note.getTitle();
        this.date = note.getDate();
        this.shared = note.getSharedUsers().size() > 1;
    }

    public boolean isShared() {
        return shared;
    }

    public String getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getDate() {
        return date;
    }
}
