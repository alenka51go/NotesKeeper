package ru.kudasheva.noteskeeper.notescroll;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.models.presentermodels.NoteShortCard;
import ru.kudasheva.noteskeeper.models.vmmodels.Card;
import ru.kudasheva.noteskeeper.models.vmmodels.User;

public class NotesScrollViewModel extends ViewModel {
    private static final String TAG = NotesScrollViewModel.class.getSimpleName();

    private static final String APP_PREFERENCES = "appsettings";
    private static final String APP_PREFERENCES_NAME = "appsettings";

    public User user = DBManager.getInstance().getUser();
    public NoteShortCard openedNote;
    public Boolean menuIsOpen = false;

    public MutableLiveData<NotesScrollViewModel.Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<List<NoteShortCard>> notes = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void updateData() {
        progressIsVisible.setValue(true);

        DBManager.getInstance().getNotes((loadedNotes) -> {
            progressIsVisible.postValue(false);

            notes.postValue(convertToShortNotes(loadedNotes));
            Log.d(TAG, "Size " + loadedNotes.size());
        });
    }

    public void onCreateNoteButtonClicked() {
        activityCommand.setValue(Commands.OPEN_CREATE_NOTE_ACTIVITY);
    }

    public void onFriendListButtonClicked() {
        activityCommand.setValue(Commands.OPEN_FRIENDS_LIST_ACTIVITY);
    }

    public void onChangeUserButtonClicked() {
        SharedPreferences mSettings = MyApplication.getInstance().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_NAME, null);
        editor.apply();

        DBManager.getInstance().resetDatabase();
        activityCommand.setValue(Commands.OPEN_LOGIN_ACTIVITY);
    }

    public void onMenuClicked() {
        if (menuIsOpen) {
            menuIsOpen = false;
            activityCommand.setValue(Commands.CLOSE_MENU);
        } else {
            menuIsOpen = true;
            activityCommand.setValue(Commands.OPEN_MENU);
        }
    }

    public void animationEnd() {
        if (!menuIsOpen) {
            activityCommand.setValue(Commands.HIDE_EXTRA_MENU_INFO);
        }
    }

    public void clickedOnNote(NoteShortCard note) {
        openedNote = note;
        activityCommand.setValue(Commands.OPEN_BROWSE_NOTE_ACTIVITY);
    }

    public enum Commands {
        OPEN_CREATE_NOTE_ACTIVITY,
        OPEN_FRIENDS_LIST_ACTIVITY,
        OPEN_LOGIN_ACTIVITY,
        OPEN_BROWSE_NOTE_ACTIVITY,
        OPEN_MENU,
        CLOSE_MENU,
        HIDE_EXTRA_MENU_INFO
    }

    private List<NoteShortCard> convertToShortNotes(List<Card> notes) {
        List<NoteShortCard> shortCards = new ArrayList<>();
        for (Card note : notes) {
            NoteShortCard noteShortCard = note.createShortCard();
            shortCards.add(noteShortCard);
        }
        return shortCards;
    }
}
