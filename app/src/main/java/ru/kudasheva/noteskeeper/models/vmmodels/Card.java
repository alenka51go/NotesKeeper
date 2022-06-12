package ru.kudasheva.noteskeeper.models.vmmodels;

import android.util.Log;

import java.util.Date;
import java.util.List;

import ru.kudasheva.noteskeeper.data.Util;
import ru.kudasheva.noteskeeper.models.datamodels.CommentData;
import ru.kudasheva.noteskeeper.models.datamodels.NoteData;
import ru.kudasheva.noteskeeper.models.datamodels.UserData;
import ru.kudasheva.noteskeeper.models.presentermodels.InfoCard;
import ru.kudasheva.noteskeeper.models.presentermodels.NoteShortCard;

public class Card {
    private static final String TAG = Card.class.getSimpleName();

    private final String documentId;
    private final User user;
    private final String title;
    private final String text;
    private final Date date;
    private final List<String> sharedUsernames;
    private final Type type;

    public Card(NoteData noteData, UserData userData) {
        user = new User(userData);
        documentId = noteData.get_id();
        title = noteData.getTitle();
        text = noteData.getText();
        date = Util.convertDate(noteData.getDate());
        sharedUsernames = noteData.getSharedUsers();
        type = Type.NOTE;
    }

    public Card(CommentData commentData, UserData userData) {
        user = new User(userData);
        documentId = commentData.get_id();
        title = null;
        text = commentData.getText();
        date = Util.convertDate(commentData.getDate());
        sharedUsernames = commentData.getSharedUsers();
        type = Type.COMMENT;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getOwnerUsername() {
        return user.getUsername();
    }

    public List<String> getSharedUsernames() {
        return sharedUsernames;
    }

    public String getDate() {
        return Util.convertDate(date);
    }

    public InfoCard createInfoCard() {
        switch (type) {
            case NOTE:
                return new InfoCard(documentId, text,
                        user.getFullName(), Util.convertDate(date), InfoCard.NOTE_ROW_TYPE);
            case COMMENT:
                return new InfoCard(documentId, text,
                        user.getFullName(), Util.convertDate(date), InfoCard.COMMENT_ROW_TYPE);
        }
        return null;
    }

    public NoteShortCard createShortCard() {
        if (type == Type.NOTE) {
            return new NoteShortCard(documentId, title,
                    getDate(), sharedUsernames.size() > 1);
        }
        Log.d(TAG, "Card can't be converted to ShortCard");
        return null;
    }

    private enum Type {
        NOTE,
        COMMENT
    }
}
