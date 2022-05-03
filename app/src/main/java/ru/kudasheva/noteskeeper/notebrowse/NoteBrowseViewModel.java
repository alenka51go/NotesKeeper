package ru.kudasheva.noteskeeper.notebrowse;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.Note;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final DataRepository dataRepo = MyApplication.getDataRepo();
    private final String username = dataRepo.getUsername();
    private String noteId;

    public String title;

    public MutableLiveData<List<InfoCard>> noteAndComments = new MutableLiveData<>();
    public MutableLiveData<String> userCommentLiveData = new MutableLiveData<>();
    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void onSendButtonClicked() {
        Log.d(TAG, username);
        Log.d(TAG, userCommentLiveData.getValue());
        Log.d(TAG, getCurrentDate());

        Map<String, Object> commentInfo = new HashMap<>();
        commentInfo.put("username", username);
        commentInfo.put("text", userCommentLiveData.getValue());
        commentInfo.put("date", getCurrentDate());

        dataRepo.addComment(noteId, commentInfo);
        noteAndComments.setValue(updateNoteAndCommentsList());

        // TODO разобраться почему не отображается очищение, хотя под капотом есть очищение
        userCommentLiveData.setValue("");
    }

    private List<InfoCard> updateNoteAndCommentsList() {

        Note note = dataRepo.getNoteById(noteId);
        title = note.getTitle();

        NoteFullCard noteFullCard = new NoteFullCard(noteId, note.getText(), note.getUsername(), note.getDate());
        List<InfoCard> noteList = new ArrayList<>(Collections.singletonList(noteFullCard));

        List<InfoCard> commentsList = new ArrayList<>();
        List<Comment> rawComments = note.getComments();
        for (Comment comment : rawComments) {
            CommentInfoCard commentInfoCard = new CommentInfoCard(comment.getText(),
                    comment.getUsername(), comment.getDate());
            commentsList.add(commentInfoCard);
        }

        noteList.addAll(commentsList);
        return noteList;
    }

    private String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return df.format(currentDate);
    }

    public void onActionMenuClicked() {
        activityCommand.setValue(Commands.SHOW_MENU);
    }

    public void deleteNote() {
        // TODO
        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
    }

    public void loadNoteInfo(String noteId) {
        this.noteId = noteId;
        noteAndComments.setValue(updateNoteAndCommentsList());
    }

    enum Commands {
        SHOW_MENU,
        CLOSE_ACTIVITY
    }
}
