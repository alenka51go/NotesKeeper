package ru.kudasheva.noteskeeper.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDocument {
    private String _id;
    private String _rev;
    private String userId;
    private String title;
    private String date;
    private String text;
    private List<String> sharedUsers;
    private final String type = "note";

    public NoteDocument() {}

    public NoteDocument(String userId, String title, String text,
                        String date, List<String> sharedUsers) {
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
    }

    public NoteDocument(String _id, String _rev, String userId, String title, String text,
                        String date, List<String> sharedUsers) {
        this._id = _id;
        this._rev = _rev;
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
    }

    public String get_id() {
        return _id;
    }

    public String getRev() {
        return _rev;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public List<String> getSharedUsers() {
        return sharedUsers;
    }
}
