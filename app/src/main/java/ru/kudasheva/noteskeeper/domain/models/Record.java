package ru.kudasheva.noteskeeper.domain.models;

import android.util.Log;

import java.util.Date;
import java.util.List;

import ru.kudasheva.noteskeeper.domain.Util;
import ru.kudasheva.noteskeeper.data.models.CommentDocument;
import ru.kudasheva.noteskeeper.data.models.NoteDocument;
import ru.kudasheva.noteskeeper.data.models.UserDocument;
import ru.kudasheva.noteskeeper.presentation.models.InfoCard;
import ru.kudasheva.noteskeeper.presentation.models.NoteShortCard;

public class Record {
    private static final String TAG = Record.class.getSimpleName();

    private final String documentId;
    private final User user;
    private final String title;
    private final String text;
    private final Date date;
    private final List<String> sharedUsernames;
    private final Type type;

    public Record(NoteDocument noteDocument, UserDocument userDocument) {
        user = new User(userDocument);
        documentId = noteDocument.get_id();
        title = noteDocument.getTitle();
        text = noteDocument.getText();
        date = Util.convertDate(noteDocument.getDate());
        sharedUsernames = noteDocument.getSharedUsers();
        type = Type.NOTE;
    }

    public Record(CommentDocument commentDocument, UserDocument userDocument) {
        user = new User(userDocument);
        documentId = commentDocument.get_id();
        title = null;
        text = commentDocument.getText();
        date = Util.convertDate(commentDocument.getDate());
        sharedUsernames = commentDocument.getSharedUsers();
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

    public Date getDate() {
        return date;
    }

    public InfoCard createInfoCard() {
        switch (type) {
            case NOTE:
                return new InfoCard(documentId, text,
                        user.getFullName(), date, InfoCard.NOTE_ROW_TYPE);
            case COMMENT:
                return new InfoCard(documentId, text,
                        user.getFullName(), date, InfoCard.COMMENT_ROW_TYPE);
        }
        return null;
    }

    public NoteShortCard createShortCard() {
        if (type == Type.NOTE) {
            return new NoteShortCard(documentId, title,
                    date, sharedUsernames.size() > 1);
        }
        Log.d(TAG, "Card can't be converted to ShortCard");
        return null;
    }

    private enum Type {
        NOTE,
        COMMENT
    }
}
