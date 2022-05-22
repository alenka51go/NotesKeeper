package ru.kudasheva.noteskeeper.data.models;

import com.fasterxml.jackson.databind.ser.std.IterableSerializer;

import java.util.List;

public class Comment {
    private String _id;
    private String _rev;
    private String noteId;
    private String userId;
    private String date;
    private String text;
    private List<String> sharedUsers;
    private final String type = "comment";

    public Comment(String id, String rev, String userId, String noteId, String text, String date, List<String> sharedUsers) {
        _id = id;
        _rev = rev;
        this.userId = userId;
        this.noteId = noteId;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
    }

    public Comment(String userId, String noteId, String text, String date, List<String> sharedUsers) {
        this.userId = userId;
        this.noteId = noteId;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
    }

    public String get_id() {
        return _id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
