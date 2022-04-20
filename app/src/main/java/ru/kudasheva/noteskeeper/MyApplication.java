package ru.kudasheva.noteskeeper;

import android.app.Application;

import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.StubDataRepository;

public class MyApplication extends Application {
    private static final DataRepository dataRepo = new StubDataRepository();

    public static DataRepository getDataRepo() {
        return dataRepo;
    }
}
