package ru.kudasheva.noteskeeper.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Note {
    private String id;
    private String revisionID;
    private String username;
    private String title;
    private String date;
    private String text;
    private List<String> friends;
    private List<Comment> comments;

    public Note() {
    }

    public Note(String username, String title, String text,
                String date, List<String> friends, List<Comment> comments) {
        this.username = username;
        this.title = title;
        this.text = text;
        this.date = date;
        this.friends = friends;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public String getRev() {
        return revisionID;
    }

    public String getUsername() {
        return username;
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

    public List<String> getFriends() {
        return friends;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
