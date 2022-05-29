package ru.kudasheva.noteskeeper.notebrowse;

import ru.kudasheva.noteskeeper.data.models.Comment;

public class CommentInfoCard extends InfoCard {
    public CommentInfoCard(String id, String note, String name, String date) {
        super(id, note, name, date);
    }

    public static CommentInfoCard from(Comment comment) {
        return new CommentInfoCard(comment.get_id(), comment.getText(),
                comment.getUserId(), comment.getDate());
    }


    @Override
    int getType() {
        return COMMENT_ROW_TYPE;
    }
}
