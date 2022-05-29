package ru.kudasheva.noteskeeper.notescroll;

public class NoteShortCard {
    private String id;
    private String header;
    private String date;

    public NoteShortCard(String id, String header, String date) {
        this.id = id;
        this.header = header;
        this.date = date;
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
