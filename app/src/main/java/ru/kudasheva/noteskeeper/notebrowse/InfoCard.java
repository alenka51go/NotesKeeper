package ru.kudasheva.noteskeeper.notebrowse;

import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.Note;

public class InfoCard {
    static int NOTE_ROW_TYPE =   0;
    static int COMMENT_ROW_TYPE = 1;

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

    public static InfoCard from(Comment comment, String username) {
        return new InfoCard(comment.get_id(), comment.getText(),
                username, comment.getDate(), COMMENT_ROW_TYPE);
    }

    public static InfoCard from(Note note, String username) {
        return new InfoCard(note.get_id(), note.getText(),
                username, note.getDate(), NOTE_ROW_TYPE);
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
