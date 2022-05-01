package ru.kudasheva.noteskeeper.notebrowse;

public class NoteFullCard extends InfoCard {
    public NoteFullCard(String note, String name, String date) {
        super(note, name, date);
    }
    @Override
    int getType() {
        return NOTE_ROW_TYPE;
    }
}
