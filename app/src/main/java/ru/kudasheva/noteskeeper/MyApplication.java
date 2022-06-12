package ru.kudasheva.noteskeeper;

import android.app.Application;

import ru.kudasheva.noteskeeper.data.DBManager;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return MyApplication.sInstance;
    }
}
