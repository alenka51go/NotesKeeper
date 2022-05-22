package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.DatabaseReplicator;
import ru.kudasheva.noteskeeper.data.models.Note;

public class DBManager {
    private static final String TAG = DBManager.class.getSimpleName();

    private static final String DATABASE_NAME = "notes_keeper_database";
    private static final String VIEW_USER_NOTES = "user_notes_view";
    private static final String VIEW_USER_COMMENTS = "user_comments_view";

    private static final String REMOTE_DATABASE_IP = "188.242.233.52";
    private static final int REMOTE_DATABASE_PORT = 5984;

    private static final String REMOTE_DATABASE_NOTES_NAME = "notes";

    private static final String NOTE_TYPE = "note";
    private static final String COMMENT_TYPE = "comment";

    private static DBManager sInstance;

    private String username;

    private Manager manager;
    private Database database;
    private DatabaseReplicator databaseReplicator;

    private View notesView;
    private View commentView;
    private LiveQuery notesQuery;
    private LiveQuery commentQuery;

    private WeakReference<NotesChangeListener> notesChangeListenerWeakRef; // ?

    public static DBManager getInstance() {
        if (sInstance == null) {
            sInstance = new DBManager();
            Log.d(TAG, "DBManager created");
        }
        return sInstance;
    }

    public void startDatabase(String inputUsername) {
        try {
            manager = new Manager(
                    new AndroidContext(MyApplication.getInstance()),
                    Manager.DEFAULT_OPTIONS
            );
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Manager: " + e.getMessage());
            e.printStackTrace();
        }

        openDatabase();

        database.addChangeListener(event -> {
            Log.d(TAG, "Got change event on user notes database");

            for (DocumentChange change : event.getChanges()) {
                Log.d(TAG, "Change: " + change);

                Document doc = database.getExistingDocument(change.getDocumentId());
                Log.d(TAG, "Doc from db: " + doc);

                Map<String, Object> properties = null;

                if (doc == null) { // Doc is deleted
                    Log.d(TAG, "Doc " + change.getDocumentId() + " is deleted");
                } else {
                    Log.d(TAG, "Doc prop: " + doc.getProperties());
                    properties = doc.getProperties();
                }

                Log.d(TAG, "Changed doc (JSON): " + properties);

                if (notesChangeListenerWeakRef != null) {
                    NotesChangeListener listener = notesChangeListenerWeakRef.get();
                    if (listener != null) {
                        // TODO: pass document parameters
                        listener.onChange(change.getDocumentId());
                    }
                }
            }
        });

        notesView = database.getView(VIEW_USER_NOTES);
        notesView.setMap((document, emitter) -> {
            if ((NOTE_TYPE).equals(document.get("type"))) {
                emitter.emit(document.get("_id"), null);
            }

            Log.d(TAG, "emitter note passing doc: " + document);
        }, "1");

        notesQuery = notesView.createQuery().toLiveQuery();
        notesQuery.start();

        commentView = database.getView(VIEW_USER_COMMENTS);
        commentView.setMap((document, emitter) -> {
            if ((COMMENT_TYPE).equals(document.get("type"))) {
                emitter.emit(document.get("_id"), null);
            }

            Log.d(TAG, "emitter comment passing doc: " + document.get("type"));
        }, "1");

        commentQuery = commentView.createQuery().toLiveQuery();
        commentQuery.start();

        try {
            databaseReplicator = new DatabaseReplicator(
                    REMOTE_DATABASE_IP,
                    REMOTE_DATABASE_PORT,
                    REMOTE_DATABASE_NOTES_NAME,
                    database
            );
            Log.d(TAG, "Database replicator was initialized");
        } catch (MalformedURLException e) {
            Log.d(TAG, "Failed to create replication for Database: " + e.getMessage());
            e.printStackTrace();
            manager.close();
            return;
        }

        username = inputUsername;

        databaseReplicator.setPullUserIdFilter(username);
        databaseReplicator.startReplication();
        Log.d(TAG, "Database replication started");
    }

    private void openDatabase() {
        try {
            database = manager.getDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to open database: " + e.getMessage());
            e.printStackTrace();
            manager.close();
        }
    }

    public void resetDatabase() {
        try {
            database.delete();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to delete CouchBase lite bases");
            e.printStackTrace();
        }

        openDatabase();
    }

    public String getUsername() {
        return username;
    }

    public void addNote(Note note) {
        final Document doc = database.createDocument();
        UnsavedRevision unsavedRevision = doc.createRevision();

        try {
            Map<String, Object> noteMap = Util.objectToMap(note);
            unsavedRevision.setProperties(noteMap);
            unsavedRevision.save();

            Log.d(TAG, "Note in map: " + note.toString());

        } catch (CouchbaseLiteException e) {
            Log.d(TAG, "ERROR - " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "Could not convert Note to Map: " + note.toString());
            e.printStackTrace();
        }

        Log.d(TAG, "Note has been added");
    }

    public void addComment(Comment comment) {
        // FIXME: комментарий отображается только после того, как перезайти в заметку
        final Document doc = database.createDocument();
        UnsavedRevision unsavedRevision = doc.createRevision();

        try {
            Map<String, Object> noteMap = Util.objectToMap(comment);
            unsavedRevision.setProperties(noteMap);
            unsavedRevision.save();

            Log.d(TAG, "Comment in map: " + comment.toString());

        } catch (CouchbaseLiteException e) {
            Log.d(TAG, "ERROR - " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "Could not convert Comment to Map: " + comment.toString());
            e.printStackTrace();
        }

        Log.d(TAG, "Comment has been added");
    }

    public boolean deleteNote(String noteId) {
        Document doc = database.getExistingDocument(noteId);
        if (doc != null) {
            Log.d(TAG, "Start deleting note with id " + noteId + " ...");

            if (Objects.equals(doc.getProperties().get("type"), NOTE_TYPE)) {
                List<Comment> commentsList = getComments(noteId);
                for (Comment comment : commentsList) {
                    deleteDoc(comment.get_id());
                }
            }
            Log.d(TAG, "All comments was successfully deleted.");

            return deleteDoc(noteId);
        } else {
            Log.d(TAG, "There is no such note in Database " + noteId);
        }
        return false;
    }

    private boolean deleteDoc(String docId) {
        try {
            Document doc = database.getExistingDocument(docId);

            if (doc != null) {
                Log.d(TAG, "Document with id " + docId + " was found. Removing...");

                boolean res = doc.delete();

                if (!res) {
                    Log.e(TAG, "Failed to remove document " + docId);

                    doc.update(newRevision -> {
                        Log.w(TAG, "Removing through update");
                        newRevision.setIsDeletion(true);
                        return true;
                    });
                }
                return true;
            } else {
                Log.d(TAG, "There is no such doc in Database " + docId);
            }
        } catch (CouchbaseLiteException e) {
            Log.d(TAG, "Failed to delete doc with id:" + docId + ". Reason: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Note getNote(String noteId) {
        Document doc = database.getExistingDocument(noteId);

        if (null != doc) {
            Log.d(TAG, "Document with id " + noteId + " was found.");
            return Util.convertToNote(doc.getProperties());
        }

        Log.d(TAG, "There is no such Note in Database + " + noteId);
        return null;
    }

    public List<Note> getUserNotes() {
        // FIXME: пока при первой загрузке данные не успевают подгрузиться на основнуб страницу
        List<Note> notes = new ArrayList<>();

        Log.d(TAG, "Load User notes: --------------");

        for (QueryRow row : notesQuery.getRows()) {
            Map<String, Object> noteProperties = row.getDocument().getProperties();
            Log.d(TAG, "type is: " + noteProperties.get("type"));
            notes.add(Util.convertToNote(noteProperties));
        }

        Log.d(TAG, "notes: " + notes.size());
        return notes;
    }

    public List<Comment> getComments(String noteId) {
        List<Comment> comments = new ArrayList<>();

        Log.d(TAG, "Load User comments: --------------");

        for (QueryRow row : commentQuery.getRows()) {
            Map<String, Object> commentProperties = row.getDocument().getProperties();
            Log.d(TAG, "type is: " + commentProperties.get("type"));
            if (noteId.equals(commentProperties.get("noteId"))) {
                comments.add(Util.convertToComment(commentProperties));
            }
        }

        Log.d(TAG, "comments: " + comments.size());
        return comments;
    }
}
