package ru.kudasheva.noteskeeper.presentation.login;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import ru.kudasheva.noteskeeper.presentation.MyApplication;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.UserDocument;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getSimpleName();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> activityCommand = new MutableLiveData<>();
    public MutableLiveData<String> username = new MutableLiveData<>();

    private static final String APP_PREFERENCES = "appsettings";
    private static final String APP_PREFERENCES_NAME = "appsettings";

    public void onLogInClicked() {
        String inputUsername = username.getValue();

        DBManager.getInstance().getUserInfoGson(inputUsername, (userInfoGson) -> {
            if (userInfoGson != null) {
                SharedPreferences mSettings = MyApplication.getInstance().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_NAME, userInfoGson);
                editor.apply();

                UserDocument user = new Gson().fromJson(userInfoGson, UserDocument.class);
                DBManager.getInstance().startReplication(user, () -> {
                    activityCommand.postValue(Commands.OPEN_NOTE_SCROLL_ACTIVITY);
                });
            } else {
                snackBarMessage.setValue(inputUsername + " doesn't exist!");
            }
        });
    }

    public enum Commands {
        OPEN_NOTE_SCROLL_ACTIVITY
    }
}
