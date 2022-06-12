package ru.kudasheva.noteskeeper.notebrowse;

import ru.kudasheva.noteskeeper.data.models.CommentData;
import ru.kudasheva.noteskeeper.data.models.NoteData;

public class InfoCard {
    public static int NOTE_ROW_TYPE =   0;
    public static int COMMENT_ROW_TYPE = 1;

    private final String id;
    private final String userName;
    private final String creationDate;
    private final String textBody;
    private final int type;

    public InfoCard(String id, String note, String name, String date, int type) {
        this.id = id;
        textBody = note;
        creationDate = date;
        userName = name;
        this.type = type;
    }

    public static InfoCard from(CommentData commentData, String username) {
        return new InfoCard(commentData.get_id(), commentData.getText(),
                username, commentData.getDate(), COMMENT_ROW_TYPE);
    }

    public static InfoCard from(NoteData noteData, String username) {
        return new InfoCard(noteData.get_id(), noteData.getText(),
                username, noteData.getDate(), NOTE_ROW_TYPE);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return userName;
    }

    public String getDate() {
        return creationDate;
    }

    public String getText() {
        return textBody;
    }

    public int getType() {
        return type;
    }
}
