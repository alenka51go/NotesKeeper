package ru.kudasheva.noteskeeper.splashscreen;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.models.UserData;

public class SplashScreenViewModel extends ViewModel {
    private static final String TAG = SplashScreenViewModel.class.getSimpleName();
    private static final String APP_PREFERENCES = "appsettings";
    private static final String APP_PREFERENCES_NAME = "appsettings";

    public MutableLiveData<SplashScreenViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void initData() {
        DBManager.getInstance().startUserDatabase();
        DBManager.getInstance().startNotesDatabase();

        SharedPreferences mSettings = MyApplication.getInstance().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String userInfoGson = mSettings.getString(APP_PREFERENCES_NAME, null);

        if (userInfoGson != null) {
            Log.d(TAG, "Save user info: " + userInfoGson);
            DBManager.getInstance().startReplication(userInfoGson);
            activityCommand.setValue(Commands.OPEN_NOTESCROLL_ACTIVITY);
        } else {
            activityCommand.setValue(Commands.OPEN_LOGIN_ACTIVITY);
        }
    }

    enum Commands {
        OPEN_LOGIN_ACTIVITY,
        OPEN_NOTESCROLL_ACTIVITY
    }
}
