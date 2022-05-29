package ru.kudasheva.noteskeeper.notebrowse;

public class NoteFullCard extends InfoCard {

    public NoteFullCard(String id, String note, String name, String date) {
        super(id, note, name, date);
    }

    @Override
    int getType() {
        return NOTE_ROW_TYPE;
    }
}
