package ru.kudasheva.noteskeeper.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.User;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getSimpleName();
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<String> firstname = new MutableLiveData<>();
    public MutableLiveData<String> lastname = new MutableLiveData<>();

    public void onLogInClicked() {
        String firstnameValue = firstname.getValue();
        String lastnameValue = lastname.getValue();

        if (firstnameValue == null || firstnameValue.isEmpty()) {
            snackBarMessage.setValue("Enter first name");
            return;
        }

        User user;
        if (lastnameValue != null && !lastnameValue.isEmpty()) {
            user = new User(firstnameValue, lastnameValue);
        } else {
            user = new User(firstnameValue);
        }


        // FIXME сделать проверку на существование пользователя в базе, видимо?
        /*if (dataRepo.checkIfUserExist(username)) {
            dataRepo.initDatabase(MyApplication.getAppContext(), username);
            activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
        } else {
            snackBarMessage.setValue(username + " doesn't exist!");
        }*/


        dataRepo.initDatabase(MyApplication.getAppContext(), user);
        activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
    }

    public void setApplicationContext(Context applicationContext) {
        MyApplication.setAppContext(applicationContext);
    }

    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY
    }
}
