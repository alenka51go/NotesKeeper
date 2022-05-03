package ru.kudasheva.noteskeeper.data;

import android.content.Context;

import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;

public interface DataRepository{
    public void initDatabase(Context context, User user);
    public void closeDatabase();

    public String getUsername();
    public Note getNoteById(String docId);
    public List<Note> getAllNotes();
    public List<String> getFriends();

    public boolean checkIfUserExist(String name);

    public boolean deleteNote(String docId);

    public void addNote(Map<String, Object> info);
    public boolean addNewFriend(String friendName);
    public boolean addComment(String docId, Map<String, Object> info);

}
