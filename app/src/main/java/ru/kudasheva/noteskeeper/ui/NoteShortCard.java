package ru.kudasheva.noteskeeper.ui;

public class NoteShortCard {
    private final String header;
    private final String date;

    public NoteShortCard(String header, String date) {
        this.header = header;
        this.date = date;
    }

    String getHeader() {
        return header;
    }

    String getDate() {
        return date;
    }
}
