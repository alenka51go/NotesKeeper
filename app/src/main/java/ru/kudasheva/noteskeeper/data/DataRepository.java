package ru.kudasheva.noteskeeper.data;

import android.content.Context;

import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.Note;

public interface DataRepository{
    public void initDatabase(Context context, String username);
    public void closeDatabase();

    public String getUsername();
    public Note getNoteById(String docId);
    public List<Note> getAllNotes();
    public List<String> getFriends();

    public boolean checkIfUserExist(String name);

    public void addNote(Map<String, Object> info);
    public boolean addNewFriend(String friendName);
    public boolean addComment(String docId, Map<String, Object> info);

}
