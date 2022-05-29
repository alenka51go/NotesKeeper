package ru.kudasheva.noteskeeper.data;

import java.util.Map;

public interface ChangeListener {
    void onChange(String documentId, Event event, Map<String, Object> type);

    enum Event {
        DELETED,
        UPDATED
    }
}
