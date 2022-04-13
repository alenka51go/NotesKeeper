package ru.kudasheva.noteskeeper;

public class CommentRowType extends RowType {
    public CommentRowType(String note, String name, String date) {
        super(note, name, date);
    }

    @Override
    int getType() {
        return COMMENT_ROW_TYPE;
    }
}
