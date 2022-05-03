package ru.kudasheva.noteskeeper.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getSimpleName();
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<String> userNameLiveData = new MutableLiveData<>();

    public void onLogInClicked() {
        String username = userNameLiveData.getValue();

        if (username == null || username.isEmpty()) {
            snackBarMessage.setValue("Enter username");
            return;
        }

        // TODO сделать проверку на существование пользователя в базе
        /*if (dataRepo.checkIfUserExist(username)) {
            dataRepo.initDatabase(MyApplication.getAppContext(), username);
            activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
        } else {
            snackBarMessage.setValue(username + " doesn't exist!");
        }*/

        dataRepo.initDatabase(MyApplication.getAppContext(), username);
        activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
    }

    public void setApplicationContext(Context applicationContext) {
        MyApplication.setAppContext(applicationContext);
    }

    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY
    }
}
