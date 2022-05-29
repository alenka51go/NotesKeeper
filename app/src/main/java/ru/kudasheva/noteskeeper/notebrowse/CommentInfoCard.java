package ru.kudasheva.noteskeeper.notebrowse;

public class CommentInfoCard extends InfoCard {
    public CommentInfoCard(String id, String note, String name, String date) {
        super(id, note, name, date);
    }

    @Override
    int getType() {
        return COMMENT_ROW_TYPE;
    }
}
