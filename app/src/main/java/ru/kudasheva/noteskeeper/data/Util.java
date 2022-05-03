package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import ru.kudasheva.noteskeeper.data.models.Friends;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.UsersBase;

public class Util {
    public static String TAG = RemoteDataRepository.class.getSimpleName();

    public static Dictionary createDictionary(Map<String, Object> info) {

        MutableDictionary dict = new MutableDictionary();

        Set<Map.Entry<String, Object>> infoSet = info.entrySet();

        for (Map.Entry<String, Object> pair : infoSet) {
            dict.setValue(pair.getKey(), pair.getValue());
        }

        return dict;
    }

    // convert from database to UI

    public static Note convertNote(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Note note = null;
        try {
            note = objectMapper.readValue(json, Note.class);
        } catch (JsonProcessingException e) {
            Log.d(TAG, e.getMessage());
        }
        return note;
    }

    public static UsersBase convertUsers(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        UsersBase users = null;
        try {
            users = objectMapper.readValue(json, UsersBase.class);
        } catch (JsonProcessingException e) {
            Log.d(TAG, e.getMessage());
        }
        return users;
    }

    public static Friends convertFriends(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Friends friends = null;
        try {
            friends = objectMapper.readValue(json, Friends.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
