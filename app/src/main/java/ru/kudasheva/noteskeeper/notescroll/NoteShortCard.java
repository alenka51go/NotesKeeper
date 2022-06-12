package ru.kudasheva.noteskeeper.notescroll;

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
