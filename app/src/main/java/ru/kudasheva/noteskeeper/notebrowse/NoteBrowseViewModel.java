package ru.kudasheva.noteskeeper.notebrowse;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.models.datamodels.CommentData;
import ru.kudasheva.noteskeeper.models.presentermodels.InfoCard;
import ru.kudasheva.noteskeeper.models.vmmodels.Card;
import ru.kudasheva.noteskeeper.models.vmmodels.User;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final User user = DBManager.getInstance().getUser();
    private Card openedNote;

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();

    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<List<InfoCard>> dataContainer = new MutableLiveData<>();
    public MutableLiveData<String> userCommentLiveData = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void initData(String noteId) {
        progressIsVisible.setValue(true);

        DBManager.getInstance().getDocument(noteId, (document) -> {
            progressIsVisible.postValue(false);

            openedNote = document.getNote();
            title.postValue(openedNote.getTitle());
            dataContainer.postValue(document.createInfoCards());
            if (!openedNote.getOwnerUsername().equals(user.getUsername())) {
                activityCommand.postValue(Commands.REMOVE_ACTION_BUTTON);
            }
        });
    }

    public void onSendButtonClicked() {
        Log.d(TAG, "Current user: " + user.getUsername());
        Log.d(TAG, "Comment: " + userCommentLiveData.getValue());
        Log.d(TAG, "Date: " + getCurrentDate());

        String commentText = userCommentLiveData.getValue();
        if (commentText == null || commentText.isEmpty()) {
            snackBarMessage.setValue("You can't send empty comment");
            return;
        }

        CommentData commentData = new CommentData(user.getUsername(), openedNote.getDocumentId(), commentText, getCurrentDate(), openedNote.getSharedUsernames());
        DBManager.getInstance().addComment(commentData);

        userCommentLiveData.setValue("");
    }

    public void update() {
        progressIsVisible.postValue(true);
        DBManager.getInstance().getDocument(openedNote.getDocumentId(), (document) -> {
            progressIsVisible.postValue(false);
            dataContainer.postValue(document.createInfoCards());
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
        DBManager.getInstance().deleteNote(openedNote.getDocumentId(), (result) -> {
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
