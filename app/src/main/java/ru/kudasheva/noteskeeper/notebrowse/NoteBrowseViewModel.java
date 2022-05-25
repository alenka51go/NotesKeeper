package ru.kudasheva.noteskeeper.notebrowse;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.Note;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final String username = DBManager.getInstance().getFullUsername();
    private String noteId;
    public String title;

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<List<InfoCard>> dataContainer = new MutableLiveData<>();
    public ObservableField<String> userCommentLiveData = new ObservableField<>();
    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void onSendButtonClicked() {
        Log.d(TAG, "Current user: " + username);
        Log.d(TAG, "Comment: " + userCommentLiveData.get());
        Log.d(TAG, "Date: " + getCurrentDate());

        String commentText = userCommentLiveData.get();
        if (commentText == null || commentText.isEmpty()) {
            snackBarMessage.setValue("You can't send empty comment");
            return;
        }

        Note note = DBManager.getInstance().getNote(noteId);
        Comment comment = new Comment(username, note.get_id(), commentText, getCurrentDate(), note.getSharedUsers());
        DBManager.getInstance().addComment(comment);

        // FIXME: не успевает обновиться локальная база
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataContainer.setValue(updateData());
        userCommentLiveData.set("");
    }

    private List<InfoCard> updateData() {
        Note note = DBManager.getInstance().getNote(noteId);

        NoteFullCard noteFullCard = new NoteFullCard(noteId, note.getText(), note.getUserId(), note.getDate());
        List<InfoCard> noteList = new ArrayList<>(Collections.singletonList(noteFullCard));

        List<InfoCard> commentsList = new ArrayList<>();

        List<Comment> rawComments = DBManager.getInstance().getComments(note.get_id());
        for (Comment comment : rawComments) {
            CommentInfoCard commentInfoCard = new CommentInfoCard(comment.getText(),
                    comment.getUserId(), comment.getDate());
            commentsList.add(commentInfoCard);
        }

        noteList.addAll(sortedCommentsByDate(commentsList));
        return noteList;
    }

    private List<InfoCard> sortedCommentsByDate(List<InfoCard> commentsList) {
        Collections.sort(commentsList, (i, j) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy 'at' h:mm a", Locale.getDefault());
            try {
                return sdf.parse(j.getDate()).compareTo(sdf.parse(i.getDate()));
            } catch (ParseException e) {
                Log.d(TAG, "Incorrect date format");
            }
            return 0;
        });
        return commentsList;
    }

    private String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' h:mm a", Locale.getDefault());
        return df.format(currentDate);
    }

    public void onActionMenuClicked() {
        activityCommand.setValue(Commands.SHOW_MENU);
    }

    public void deleteNote() {
        if (DBManager.getInstance().deleteNote(noteId)) {
            Log.d(TAG, "Can't delete note");
        }
        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
    }

    public void preloadData(String id) {
        noteId = id;

        dataContainer.setValue(updateData());

        Note note = DBManager.getInstance().getNote(noteId);
        title = note.getTitle();

        if (!note.getUserId().equals(username)) {
            activityCommand.setValue(Commands.REMOVE_ACTION_BUTTON);
        }
    }

    enum Commands {
        SHOW_MENU,
        CLOSE_ACTIVITY,
        REMOVE_ACTION_BUTTON
    }
}
