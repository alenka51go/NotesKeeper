package ru.kudasheva.noteskeeper.data;

import java.util.Map;

public interface FriendChangeListener {
    void onChange(String documentId, Map<String, Object> type);
}
