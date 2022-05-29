package ru.kudasheva.noteskeeper;

import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DBManager.getInstance();
    }

    public static MyApplication getInstance() {
        return MyApplication.sInstance;
    }
}
