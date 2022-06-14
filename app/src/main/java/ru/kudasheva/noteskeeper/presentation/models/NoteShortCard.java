package ru.kudasheva.noteskeeper.presentation.models;

import java.util.Date;

import ru.kudasheva.noteskeeper.domain.Util;

public class NoteShortCard {
    private final String id;
    private final String header;
    private final Date date;
    private final boolean shared;

    public NoteShortCard(String id, String header, Date date, boolean shared) {
        this.id = id;
        this.header = header;
        this.date = date;
        this.shared = shared;
    }

    public NoteShortCard(String id, String header, String dateString, boolean shared) {
        this.id = id;
        this.header = header;
        this.date = Util.convertDate(dateString);
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
        return Util.convertDatePresentation(date);
    }

    public Date getDateInfo() {
        return date;
    }
}
