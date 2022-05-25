package ru.kudasheva.noteskeeper.splashscreen;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.kudasheva.noteskeeper.data.DBManager;

public class SplashScreenViewModel extends ViewModel {
    private static final String TAG = SplashScreenViewModel.class.getSimpleName();

    public MutableLiveData<SplashScreenViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void init() {
        // FIXME: где сохранить файл с именем и только ли с именем?
        /*FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("../temp/username");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "User doesn't log in");
            activityCommand.setValue(Commands.OPEN_LOGIN_ACTIVITY);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder usernameBuilder = new StringBuilder();
        while (true) {
            String line;
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                Log.d(TAG, "Can't load username from file");
                break;
            }
            usernameBuilder.append(line);
        }

        String username = usernameBuilder.toString();*/

        String username = "testUserId";
        DBManager.getInstance().startNotesDatabase(username);
        activityCommand.setValue(Commands.OPEN_NOTESCROLL_ACTIVITY);
    }

    enum Commands {
        OPEN_LOGIN_ACTIVITY,
        OPEN_NOTESCROLL_ACTIVITY
    }
}
