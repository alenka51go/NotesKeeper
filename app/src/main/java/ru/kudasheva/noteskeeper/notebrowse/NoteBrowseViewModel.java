package ru.kudasheva.noteskeeper.notebrowse;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;

public class NoteBrowseViewModel  extends ViewModel {
    private static final String TAG = NoteBrowseViewModel.class.getSimpleName();

    private final DataRepository dataRepo = MyApplication.getDataRepo();
    private final String username = dataRepo.getUsername();

    public String title;

    public MutableLiveData<List<InfoCard>> noteAndComments = new MutableLiveData<>();
    public MutableLiveData<String> userCommentLiveData = new MutableLiveData<>();
    public MutableLiveData<NoteBrowseViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void onSendButtonClicked() {
        Log.d(TAG, username);
        Log.d(TAG, userCommentLiveData.getValue());
        Log.d(TAG, getCurrentDate());

        CommentInfoCard comment = new CommentInfoCard(userCommentLiveData.getValue(), username, getCurrentDate());
        dataRepo.addComment(comment);
        noteAndComments.setValue(updateNoteAndCommentsList());

        // TODO разобраться почему не отображается очищение, хотя под капотом есть очищение
        userCommentLiveData.setValue("");
    }

    private List<InfoCard> updateNoteAndCommentsList() {
        List<InfoCard> noteList = new ArrayList<>(Collections.singletonList(dataRepo.getNoteFullCard(title)));
        List<InfoCard> commentsList = dataRepo.getListOfCommentInfoCard();
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
        /// TODO
        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
    }

    public void loadNoteInfo(String title) {
        this.title = title;
        noteAndComments.setValue(updateNoteAndCommentsList());
    }

    enum Commands {
        SHOW_MENU,
        CLOSE_ACTIVITY
    }
}
