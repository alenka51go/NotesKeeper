package ru.kudasheva.noteskeeper.notebrowse;

public class NoteFullCard extends InfoCard {
    private final String id;

    public NoteFullCard(String id, String note, String name, String date) {
        super(note, name, date);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    int getType() {
        return NOTE_ROW_TYPE;
    }
}
