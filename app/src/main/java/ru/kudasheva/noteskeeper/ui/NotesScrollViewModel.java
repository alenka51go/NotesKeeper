package ru.kudasheva.noteskeeper.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;

public class NotesScrollViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public MutableLiveData<NotesScrollViewModel.Commands> activityCommand = new MutableLiveData<>();
    public String username;

    public void startPosition() {
        activityCommand.setValue(Commands.MAKE_INITIALIZATION);
        username = dataRepo.getUserName();
    }

    public void loadShortNodes() {
        dataRepo.getListOfNoteShortCard();
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



    public enum Commands {
        MAKE_INITIALIZATION,
        OPEN_CREATE_NOTE_ACTIVITY,
        OPEN_FRIENDS_LIST_ACTIVITY,
        OPEN_LOGIN_ACTIVITY
    }
}
