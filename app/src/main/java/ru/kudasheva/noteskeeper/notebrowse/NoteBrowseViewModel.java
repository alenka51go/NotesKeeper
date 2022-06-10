package ru.kudasheva.noteskeeper.notebrowse;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
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
import ru.kudasheva.noteskeeper.data.models.User;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final String username = DBManager.getInstance().getUsername();
    private final String fullUsername = DBManager.getInstance().getFullUsername();
    private Note openedNote;

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();

    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<List<InfoCard>> dataContainer = new MutableLiveData<>();
    public MutableLiveData<String> userCommentLiveData = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void initData(String noteId) {
        progressIsVisible.setValue(true); // TODO установить крутилку

        DBManager.getInstance().getFullNoteData(noteId, (fullNoteData) -> {
            progressIsVisible.postValue(false);

            openedNote = fullNoteData.note;
            title.postValue(fullNoteData.note.getTitle());
            dataContainer.postValue(fullNoteData.createInfoCards());
            if (!fullNoteData.note.getUserId().equals(username)) {
                activityCommand.setValue(Commands.REMOVE_ACTION_BUTTON);
            }
        });
    }

    public void onSendButtonClicked() {
        Log.d(TAG, "Current user: " + username);
        Log.d(TAG, "Comment: " + userCommentLiveData.getValue());
        Log.d(TAG, "Date: " + getCurrentDate());

        String commentText = userCommentLiveData.getValue();
        if (commentText == null || commentText.isEmpty()) {
            snackBarMessage.setValue("You can't send empty comment");
            return;
        }

        Comment comment = new Comment(username, openedNote.get_id(), commentText, getCurrentDate(), openedNote.getSharedUsers());
        DBManager.getInstance().addComment(comment);

        userCommentLiveData.setValue("");
    }

    public void update() {
        progressIsVisible.postValue(true);
        DBManager.getInstance().getFullNoteData(openedNote.get_id(), (fullNoteData) -> {
            progressIsVisible.postValue(false);
            dataContainer.postValue(fullNoteData.createInfoCards());
        });
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
        progressIsVisible.postValue(true);
        DBManager.getInstance().deleteNote(openedNote.get_id(), (result) -> {
            progressIsVisible.postValue(false);
            if (!result) {
                Log.d(TAG, "Can't delete note");
            }
            activityCommand.postValue(Commands.CLOSE_ACTIVITY);
        });
    }

    enum Commands {
        SHOW_MENU,
        CLOSE_ACTIVITY,
        REMOVE_ACTION_BUTTON
    }
}
