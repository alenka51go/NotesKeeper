package ru.kudasheva.noteskeeper.data;

import android.os.Build;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.Note;

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

    public static Note convertToNote(Map<String, Object> properties) {
        return new Note((String) properties.get("_id"),  (String) properties.get("_rev"),
                (String) properties.get("userId"), (String) properties.get("title"),
                (String) properties.get("text"), (String) properties.get("date"),
                (List<String>) properties.get("friends"));
    }
}
