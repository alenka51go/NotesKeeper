package ru.kudasheva.noteskeeper.notescroll;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;

public class NotesScrollViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public String username = dataRepo.getUsername();
    private boolean isMenuOpen = false;

    public MutableLiveData<List<NoteShortCard>> notes = new MutableLiveData<>(loadShortNodes());
    public MutableLiveData<NotesScrollViewModel.Commands> activityCommand = new MutableLiveData<>();
    public NoteShortCard noteToShow;

    public List<NoteShortCard> loadShortNodes() {
        return dataRepo.getListOfNoteShortCard();
    }

    public void onCreateNoteButtonClicked() {
        activityCommand.setValue(Commands.OPEN_CREATE_NOTE_ACTIVITY);
    }

    public void onFriendListButtonClicked() {
        activityCommand.setValue(Commands.OPEN_FRIENDS_LIST_ACTIVITY);
    }

    public void onChangeUserButtonClicked() {
        activityCommand.setValue(Commands.OPEN_LOGIN_ACTIVITY);
    }

    public void onMenuClicked() {
        if (isMenuOpen) {
            activityCommand.setValue(Commands.CLOSE_MENU);
            isMenuOpen = false;
        } else {
            activityCommand.setValue(Commands.OPEN_MENU);
            isMenuOpen = true;
        }
    }

    public void animationEnd() {
        if (!isMenuOpen) {
            activityCommand.setValue(Commands.HIDE_EXTRA_MENU_INFO);
        }
    }

    public void clickedOnNote(NoteShortCard note) {
        // TODO load note somewhere
        noteToShow = note;
        activityCommand.setValue(Commands.OPEN_BROWSE_NOTE_ACTIVITY);
    }

    public void onResume() {
        // подгружаем заметки пользователя
        notes.setValue(loadShortNodes());
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
}
