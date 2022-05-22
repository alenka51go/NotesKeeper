package ru.kudasheva.noteskeeper.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Note {
    private String id;
    private String revisionID;
    private String userId;
    private String title;
    private String date;
    private String text;
    private List<String> sharedUsers;
    private List<Comment> comments;

    public Note() {
    }

    public Note(String userId, String title, String text,
                String date, List<String> sharedUsers, List<Comment> comments) {
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
        this.comments = comments;
    }

    public Note(String id, String revisionID, String userId, String title, String text,
                String date, List<String> sharedUsers) {
        this.id = id;
        this.revisionID = revisionID;
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.sharedUsers = sharedUsers;
        this.comments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getRev() {
        return revisionID;
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

    public List<Comment> getComments() {
        return comments;
    }
}
