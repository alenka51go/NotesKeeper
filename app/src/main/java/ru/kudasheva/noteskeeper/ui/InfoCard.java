package ru.kudasheva.noteskeeper.ui;

public abstract class InfoCard {
    static int NOTE_ROW_TYPE =   0;
    static int COMMENT_ROW_TYPE = 1;

    private final String userName;
    private final String creationDate;
    private final String textBody;

    public InfoCard(String note, String name, String date) {
        textBody = note;
        creationDate = date;
        userName = name;
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