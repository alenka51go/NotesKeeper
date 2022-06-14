package ru.kudasheva.noteskeeper.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.domain.Util;
import ru.kudasheva.noteskeeper.data.models.CommentDocument;
import ru.kudasheva.noteskeeper.data.models.NoteDocument;
import ru.kudasheva.noteskeeper.data.models.UserDocument;
import ru.kudasheva.noteskeeper.presentation.models.InfoCard;

public class FullRecord {
    private static final String TAG = FullRecord.class.getSimpleName();

    private final Record note;
    private final List<Record> comments;

    public FullRecord(UserDocument noteOwner, NoteDocument userNoteDocument, Map<CommentDocument, UserDocument> userComments) {
        note = new Record(userNoteDocument, noteOwner);
        comments = new ArrayList<>();
        for (Map.Entry<CommentDocument, UserDocument> pair : userComments.entrySet()) {
            comments.add(new Record(pair.getKey(), pair.getValue()));
        }
    }

    public Record getNote() {
        return note;
    }

    public List<Record> getComments() {
        return comments;
    }

    public List<InfoCard> createInfoCards() {
        InfoCard noteFullCard = note.createInfoCard();
        List<InfoCard> resultList = new ArrayList<>(Collections.singletonList(noteFullCard));

        List<InfoCard> commentsList = new ArrayList<>();
        for (Record comment : comments) {
            InfoCard commentInfoCard = comment.createInfoCard();
            commentsList.add(commentInfoCard);
        }

        resultList.addAll(sortedCommentsByDate(commentsList));
        return resultList;
    }

    private List<InfoCard> sortedCommentsByDate(List<InfoCard> commentsList) {
        Collections.sort(commentsList, Comparator.comparing(InfoCard::getDateInfo));
        return commentsList;
    }
}
