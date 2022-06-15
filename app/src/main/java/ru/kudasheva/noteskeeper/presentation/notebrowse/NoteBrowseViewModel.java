package ru.kudasheva.noteskeeper.presentation.notebrowse;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.CommentDocument;
import ru.kudasheva.noteskeeper.domain.Util;
import ru.kudasheva.noteskeeper.domain.models.Record;
import ru.kudasheva.noteskeeper.presentation.models.InfoCard;
import ru.kudasheva.noteskeeper.domain.models.User;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final User user = DBManager.getInstance().getUser();
    private Record openedNote;

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();

    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<List<InfoCard>> dataContainer = new MutableLiveData<>();
    public MutableLiveData<String> userCommentLiveData = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void initData(String noteId) {
        progressIsVisible.setValue(true);

        DBManager.getInstance().getFullRecord(noteId, (document) -> {
            progressIsVisible.postValue(false);

            openedNote = document.getNote();
            if (!openedNote.getOwnerUsername().equals(user.getUsername())) {
                activityCommand.postValue(Commands.REMOVE_ACTION_BUTTON);
            }
            title.postValue(openedNote.getTitle());
            dataContainer.postValue(document.createInfoCards());
        });
    }

    public void onSendButtonClicked() {
        Log.d(TAG, "Current user: " + user.getUsername());
        Log.d(TAG, "Comment: " + userCommentLiveData.getValue());
        Log.d(TAG, "Date: " + Util.getCurrentDate());

        String commentText = userCommentLiveData.getValue();
        if (commentText == null || commentText.isEmpty()) {
            snackBarMessage.setValue("You can't send empty comment");
            return;
        }

        CommentDocument commentDocument =
                new CommentDocument(user.getUsername(), openedNote.getDocumentId(),
                        commentText, Util.getCurrentDate(), openedNote.getSharedUsernames());
        DBManager.getInstance().addComment(commentDocument);

        userCommentLiveData.setValue("");
    }

    public void update() {
        progressIsVisible.postValue(true);
        DBManager.getInstance().getFullRecord(openedNote.getDocumentId(), (document) -> {
            progressIsVisible.postValue(false);
            dataContainer.postValue(document.createInfoCards());
        });
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
