package ru.kudasheva.noteskeeper.data;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;

public class Util {

    public static <T> Map<String, Object> objectToMap(T obj) throws JSONException {
        String jsonString = new Gson().toJson(obj);
        Map<String, Object> docProperties = jsonObjectToMap(new JSONObject(jsonString));

        // Exclude DB properties
        docProperties.remove("_id");
        docProperties.remove("_rev");
        return docProperties;
    }

    private static Map<String, Object> jsonObjectToMap(JSONObject jObj) throws JSONException {
        Map<String, Object> properties = new HashMap<>();

        Iterator<String> iterator = jObj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jObj.get(key);

            if (value instanceof JSONObject) {
                properties.put(key, jsonObjectToMap((JSONObject) value));
                continue;
            } else if (value instanceof JSONArray) {
                properties.put(key, jsonObjectToArray((JSONArray) value));
                continue;
            }

            properties.put(key, value);
        }

        return properties;
    }

    private static List<Object> jsonObjectToArray(JSONArray jArray) throws JSONException {
        List<Object> objects = new ArrayList<>(jArray.length());

        for (int i = 0; i < jArray.length(); i++) {
            objects.add(jArray.get(i));
        }

        return objects;
    }

    public static Note convertToNote(Map<String, Object> noteProperties) {
        return new Note((String) noteProperties.get("_id"),  (String) noteProperties.get("_rev"),
                (String) noteProperties.get("userId"), (String) noteProperties.get("title"),
                (String) noteProperties.get("text"), (String) noteProperties.get("date"),
                (List<String>) noteProperties.get("sharedUsers"));
    }

    public static Comment convertToComment(Map<String, Object> commentProperties) {
        return new Comment((String) commentProperties.get("_id"),  (String) commentProperties.get("_rev"),
                (String) commentProperties.get("userId"), (String) commentProperties.get("noteId"),
                (String) commentProperties.get("text"), (String) commentProperties.get("date"),
                (List<String>) commentProperties.get("sharedUsers"));
    }

    public static User convertToUser(Map<String, Object> userProperties) {
        return new User((String) userProperties.get("_id"),  (String) userProperties.get("_rev"),
                (String) userProperties.get("firstname"), (String) userProperties.get("lastname"),
                (List<String>) userProperties.get("friends"));
    }
}
