package ru.kudasheva.noteskeeper.splashscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DBManager;

public class SplashScreenViewModel extends ViewModel {
    private static final String TAG = SplashScreenViewModel.class.getSimpleName();
    private static final String APP_PREFERENCES = "appsettings";
    private static final String APP_PREFERENCES_NAME = "appsettings";

    public MutableLiveData<SplashScreenViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void init() {
        DBManager.getInstance().startUserDatabase();
        DBManager.getInstance().startNotesDatabase();

        SharedPreferences mSettings = MyApplication.getInstance().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String username = mSettings.getString(APP_PREFERENCES_NAME, null);

        if (username != null) {
            DBManager.getInstance().startReplication(username);
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
