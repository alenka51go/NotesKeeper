package ru.kudasheva.noteskeeper;

public class NoteRowType extends RowType {
    public NoteRowType(String note, String name, String date) {
        super(note, name, date);
    }
    @Override
    int getType() {
        return NOTE_ROW_TYPE;
    }
}
