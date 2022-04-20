package ru.kudasheva.noteskeeper.viewmodel;



import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;

public class LoginViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> openActivityCommand = new MutableLiveData<>();
    public MutableLiveData<String> userNameLiveData = new MutableLiveData<>();

    public void onLogInClicked() {
        String username = userNameLiveData.getValue();

        if (dataRepo.checkIfUserExist(username)) {
            openActivityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
        } else {
            snackBarMessage.setValue(username + " doesn't exist!");
        }
    }

    public void onSignUpClicked(View view) {
        openActivityCommand.setValue(Commands.OPEN_DIALOG);
    }

    public boolean checkPossibilityOfAdditionNewUser(String newUsername) {
        if (dataRepo.signUpNewUser(newUsername)) {
            openActivityCommand.setValue(Commands.CLOSE_DIALOG);
            return true;
        } else {
            snackBarMessage.setValue(newUsername + " already exist!");
            return false;
        }
    }

    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY,
        OPEN_DIALOG,
        CLOSE_DIALOG
    }
}