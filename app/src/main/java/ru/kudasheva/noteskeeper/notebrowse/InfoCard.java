package ru.kudasheva.noteskeeper.notebrowse;

public abstract class InfoCard {
    static int NOTE_ROW_TYPE =   0;
    static int COMMENT_ROW_TYPE = 1;

    private final String id;
    private final String userName;
    private final String creationDate;
    private final String textBody;

    public InfoCard(String id, String note, String name, String date) {
        this.id = id;
        textBody = note;
        creationDate = date;
        userName = name;
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

    abstract int getType();
}
