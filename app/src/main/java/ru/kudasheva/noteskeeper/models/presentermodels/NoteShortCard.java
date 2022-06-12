package ru.kudasheva.noteskeeper.models.presentermodels;

public class NoteShortCard {
    private final String id;
    private final String header;
    private final String date;
    private final boolean shared;

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
