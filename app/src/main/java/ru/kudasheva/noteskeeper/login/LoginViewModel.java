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

        // FIXME сделать проверку на существование пользователя в базе, видимо?
        // TODO тут произвести загрузку пользователя из базы
        User user = null;
        /*if (dataRepo.checkIfUserExist(username)) {
            dataRepo.initDatabase(MyApplication.getAppContext(), username);
            activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
        } else {
            snackBarMessage.setValue(username + " doesn't exist!");
        }*/

        DBManager.getInstance().startNotesDatabase(inputUsername);
        activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
    }


    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY
    }
}
