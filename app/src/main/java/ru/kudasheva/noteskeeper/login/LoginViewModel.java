package ru.kudasheva.noteskeeper.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.User;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getSimpleName();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<String> username = new MutableLiveData<>();

    public void onLogInClicked() {
        String inputUsername = username.getValue();

        if (DBManager.getInstance().checkIfUserExist(inputUsername)) {
            DBManager.getInstance().startNotesDatabase(inputUsername);
            activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
        } else {
            snackBarMessage.setValue(inputUsername + " doesn't exist!");
        }
    }

    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY
    }
}
