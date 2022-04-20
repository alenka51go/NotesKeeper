package ru.kudasheva.noteskeeper.ui;

public class CommentInfoCard extends InfoCard {
    public CommentInfoCard(String note, String name, String date) {
        super(note, name, date);
    }

    @Override
    int getType() {
        return COMMENT_ROW_TYPE;
    }
}
