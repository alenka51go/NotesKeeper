package ru.kudasheva.noteskeeper.data;

import java.util.List;

public class NoteData {
    private String username;
    private String title;
    private String text;
    private String date;
    private List<String> friendsAllowedToComment;

    public NoteData(String username, String title, String text, String date, List<String> friendsAllowedToComment) {
        this.username = username;
        this.title = title;
        this.text = text;
        this.date = date;
        this.friendsAllowedToComment = friendsAllowedToComment;
    }

}
