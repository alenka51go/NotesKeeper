package ru.kudasheva.noteskeeper;

import android.app.Application;
import android.content.Context;

import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.RemoteDataRepository;

public class MyApplication extends Application {
    private static final DataRepository dataRepo = new RemoteDataRepository();
    private static Context appContext;

    public static DataRepository getDataRepo() {
        return dataRepo;
    }
    public static void setAppContext(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }
    public static Context getAppContext() {
        return appContext;
    }
}
