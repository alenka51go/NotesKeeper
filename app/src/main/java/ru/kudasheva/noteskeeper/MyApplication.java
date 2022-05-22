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
    private static final DataRepository dataRepo = new DataRepository() {
        @Override
        public void initDatabase(Context context, User user) {

        }

        @Override
        public void closeDatabase() {

        }

        @Override
        public String getUsername() {
            return null;
        }

        @Override
        public Note getNoteById(String docId) {
            return null;
        }

        @Override
        public List<Note> getAllNotes() {
            return null;
        }

        @Override
        public List<String> getFriends() {
            return null;
        }

        @Override
        public boolean checkIfUserExist(String name) {
            return false;
        }

        @Override
        public boolean deleteNote(String docId) {
            return false;
        }

        @Override
        public void addNote(Map<String, Object> info) {

        }

        @Override
        public boolean addNewFriend(String friendName) {
            return false;
        }

        @Override
        public boolean addComment(String docId, Map<String, Object> info) {
            return false;
        }
    };

    public static DataRepository getDataRepo() {
        return dataRepo;
    }

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
