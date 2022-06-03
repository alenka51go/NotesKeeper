package ru.kudasheva.noteskeeper.notebrowse;

import ru.kudasheva.noteskeeper.data.models.Note;

public class NoteFullCard extends InfoCard {

    public NoteFullCard(String id, String note, String name, String date) {
        super(id, note, name, date);
    }

    public static NoteFullCard from(Note note, String username) {
        return new NoteFullCard(note.get_id(), note.getText(), username, note.getDate());
    }

    @Override
    int getType() {
        return NOTE_ROW_TYPE;
    }
}
