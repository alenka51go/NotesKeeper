package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class CommentDocument {
    private String _id;
    private String _rev;
    private String noteId;
    private String userId;
    private String date;
    private String text;
    private List<String> sharedUsers;
    private String deleted = "0";
    private final String type = "comment";

    public CommentDocument(String id, String rev, String userId, String noteId, String text,
                           String date, List<String> sharedUsers, String deleted) {
        _id = id;
        _rev = rev;
        this.userId = userId;
        this.noteId = noteId;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
        this.deleted = deleted;
    }

    public CommentDocument(String userId, String noteId, String text, String date,
                           List<String> sharedUsers) {
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

    public List<String> getSharedUsers() {
        return sharedUsers;
    }

    public void delete() {
        deleted = "1";
    }

    public boolean isDeleted() {
        return deleted.equals("1");
    }
}
