package ru.kudasheva.noteskeeper.presentation.models;

import java.util.Date;

import ru.kudasheva.noteskeeper.data.models.CommentDocument;
import ru.kudasheva.noteskeeper.data.models.NoteDocument;
import ru.kudasheva.noteskeeper.domain.Util;

public class InfoCard {
    public static int NOTE_ROW_TYPE =   0;
    public static int COMMENT_ROW_TYPE = 1;

    private final String id;
    private final String userName;
    private final Date creationDate;
    private final String textBody;
    private final int type;

    public InfoCard(String id, String note, String name, Date date, int type) {
        this.id = id;
        textBody = note;
        creationDate = date;
        userName = name;
        this.type = type;
    }

    public InfoCard(String id, String note, String name, String date, int type) {
        this.id = id;
        textBody = note;
        creationDate = Util.convertDate(date);
        userName = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return userName;
    }

    public String getDate() {
        return Util.convertDatePresentation(creationDate);
    }

    public Date getDateInfo() {
        return creationDate;
    }

    public String getText() {
        return textBody;
    }

    public int getType() {
        return type;
    }
}
