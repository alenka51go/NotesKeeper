package ru.kudasheva.noteskeeper.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;

public class LoginViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public MutableLiveData<String> snackBarMessage = new MutableLiveData<>();
    public MutableLiveData<String> userNameLiveData = new MutableLiveData<>();
    public MutableLiveData<Commands> openActivityCommand = new MutableLiveData<>();

    public void onLogInClicked() {
        String username = userNameLiveData.getValue();

        if (dataRepo.checkIfUserExist(username)) {
            openActivityCommand.postValue(Commands.openNoteScrollActivity);
        } else {
            snackBarMessage.setValue(username + " doesn't exist!");
        }
    }

    public void onSignUpClicked(View view) {
        Log.d("TEST_TAG", "onSignUp()");
        openActivityCommand.postValue(Commands.openDialog);
    }

    public boolean checkPossibilityOfAdditionNewUser(String newUsername) {
        if (dataRepo.signUpNewUser(newUsername)) {
            return true;
        } else {
            snackBarMessage.setValue(newUsername + " already exist!");
            return false;
        }
    }

    public enum Commands {
        openNoteScrollActivity,
        openDialog
    }
}
