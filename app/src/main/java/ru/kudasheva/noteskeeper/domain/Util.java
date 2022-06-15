package ru.kudasheva.noteskeeper.domain;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.CommentDocument;
import ru.kudasheva.noteskeeper.data.models.NoteDocument;
import ru.kudasheva.noteskeeper.data.models.UserDocument;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

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

    public static NoteDocument convertToNote(Map<String, Object> noteProperties) {
        return new NoteDocument((String) noteProperties.get("_id"),  (String) noteProperties.get("_rev"),
                (String) noteProperties.get("userId"), (String) noteProperties.get("title"),
                (String) noteProperties.get("text"), (String) noteProperties.get("date"),
                (List<String>) noteProperties.get("sharedUsers"), (String) noteProperties.get("deleted"));
    }

    public static CommentDocument convertToComment(Map<String, Object> commentProperties) {
        return new CommentDocument((String) commentProperties.get("_id"),  (String) commentProperties.get("_rev"),
                (String) commentProperties.get("userId"), (String) commentProperties.get("noteId"),
                (String) commentProperties.get("text"), (String) commentProperties.get("date"),
                (List<String>) commentProperties.get("sharedUsers"), (String) commentProperties.get("deleted"));
    }

    public static UserDocument convertToUser(Map<String, Object> userProperties) {
        return new UserDocument((String) userProperties.get("_id"),  (String) userProperties.get("_rev"),
                (String) userProperties.get("firstname"), (String) userProperties.get("lastname"),
                (List<String>) userProperties.get("friends"));
    }

    public static String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return df.format(currentDate);
    }

    public static Date convertDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.d(TAG, "Incorrect date format");
        }
        return null;
    }

    public static String convertDatePresentation(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' h:mm a", Locale.getDefault());
        return df.format(date);
    }
}
