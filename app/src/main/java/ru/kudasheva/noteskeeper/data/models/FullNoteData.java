package ru.kudasheva.noteskeeper.data.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kudasheva.noteskeeper.notebrowse.InfoCard;

public class FullNoteData {
    private static final String TAG = FullNoteData.class.getSimpleName();

    public User owner;
    public Note note;
    public Map<Comment, User> userCommentMap;

    public FullNoteData(User noteOwner, Note userNote, Map<Comment, User> userComments) {
        owner = noteOwner;
        note = userNote;
        userCommentMap = userComments;
    }

    public List<InfoCard> createInfoCards() {
        InfoCard noteFullCard = InfoCard.from(note, owner.getFullUsername());
        List<InfoCard> resultList = new ArrayList<>(Collections.singletonList(noteFullCard));

        List<InfoCard> commentsList = new ArrayList<>();

        for (Map.Entry<Comment, User> entry : userCommentMap.entrySet()) {
            InfoCard commentInfoCard = InfoCard.from(entry.getKey(), entry.getValue().getFullUsername());
            commentsList.add(commentInfoCard);
        }

        resultList.addAll(sortedCommentsByDate(commentsList));
        return resultList;
    }

    private List<InfoCard> sortedCommentsByDate(List<InfoCard> commentsList) {
        Collections.sort(commentsList, (i, j) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' h:mm a", Locale.getDefault());
            try {
                return sdf.parse(i.getDate()).compareTo(sdf.parse(j.getDate()));
            } catch (ParseException e) {
                Log.d(TAG, "Incorrect date format");
            }
            return 0;
        });
        return commentsList;
    }
}
