package ru.kudasheva.noteskeeper.vmmodels;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.Util;
import ru.kudasheva.noteskeeper.data.models.CommentData;
import ru.kudasheva.noteskeeper.data.models.NoteData;
import ru.kudasheva.noteskeeper.data.models.UserData;
import ru.kudasheva.noteskeeper.notebrowse.InfoCard;

public class Document {
    private static final String TAG = Document.class.getSimpleName();

    private Card note;
    private List<Card> comments;

    public Document(UserData noteOwner, NoteData userNoteData, Map<CommentData, UserData> userComments) {
        note = new Card(userNoteData, noteOwner);
        comments = new ArrayList<>();
        for (Map.Entry<CommentData, UserData> pair : userComments.entrySet()) {
            comments.add(new Card(pair.getKey(), pair.getValue()));
        }
    }

    public Card getNote() {
        return note;
    }

    public List<Card> getComments() {
        return comments;
    }

    public List<InfoCard> createInfoCards() {
        InfoCard noteFullCard = note.createInfoCard();
        List<InfoCard> resultList = new ArrayList<>(Collections.singletonList(noteFullCard));

        List<InfoCard> commentsList = new ArrayList<>();
        for (Card comment : comments) {
            InfoCard commentInfoCard = comment.createInfoCard();
            commentsList.add(commentInfoCard);
        }

        resultList.addAll(sortedCommentsByDate(commentsList));
        return resultList;
    }

    private List<InfoCard> sortedCommentsByDate(List<InfoCard> commentsList) {
        Collections.sort(commentsList, (i, j) -> {
            Date iData = Util.convertDate(i.getDate());
            Date jData = Util.convertDate(j.getDate());
            return iData.compareTo(jData);
        });
        return commentsList;
    }
}
