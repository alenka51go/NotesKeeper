package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.models.CommentData;
import ru.kudasheva.noteskeeper.data.models.UserData;
import ru.kudasheva.noteskeeper.vmmodels.Document;
import ru.kudasheva.noteskeeper.data.models.NoteData;
import ru.kudasheva.noteskeeper.friends.FriendsViewModel;
import ru.kudasheva.noteskeeper.vmmodels.Card;
import ru.kudasheva.noteskeeper.vmmodels.User;

public class DBManager {
    private static final String TAG = DBManager.class.getSimpleName();

    private static final String DATABASE_NAME = "notes_keeper_database";
    private static final String USER_DATABASE_NAME = "user_database";

    private static final String VIEW_USER_NOTES = "user_notes_view";
    private static final String VIEW_USER_COMMENTS = "user_comments_view";

    private static final String REMOTE_DATABASE_IP = "188.242.233.52";
    private static final int REMOTE_DATABASE_PORT = 5984;

    private static final String REMOTE_DATABASE_NOTES_NAME = "notes";
    private static final String REMOTE_DATABASE_USERS_NAME = "noteskeeperusers";

    private static final String NOTE_TYPE = "note";
    private static final String COMMENT_TYPE = "comment";

    private static DBManager sInstance;

    private UserData currentUserData;

    private Manager manager;
    private Database database;
    private DatabaseReplicator databaseReplicator;

    private Manager userManager;
    private Database userDatabase;
    private DatabaseReplicator userDatabaseReplicator;

    private View notesView;
    private View commentView;
    private LiveQuery notesQuery;
    private LiveQuery commentQuery;

    private final Map<Integer, WeakReference<ChangeListener>> changeNoteListenerWeakRef = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable ->
            new Thread(runnable, "DatabaseExecutor"));

    public static DBManager getInstance() {
        if (sInstance == null) {
            sInstance = new DBManager();
            Log.d(TAG, "DBManager created");
        }
        return sInstance;
    }

    public void setNotesChangeListener(int hash, ChangeListener listener) {
        changeNoteListenerWeakRef.put(hash, new WeakReference<>(listener));
    }

    public void startNotesDatabase() {
        executor.submit(this::startNotesDB);
    }

    public void startUserDatabase() {
        executor.submit(this::startUserDB);
    }

    private void startUserDB() {
        try {
            userManager = new Manager(
                    new AndroidContext(MyApplication.getInstance()),
                    Manager.DEFAULT_OPTIONS
            );
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Manager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            userDatabase = userManager.getDatabase(USER_DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to open database: " + e.getMessage());
            e.printStackTrace();
            userManager.close();
        }

        try {
            userDatabaseReplicator = new DatabaseReplicator(
                    REMOTE_DATABASE_IP,
                    REMOTE_DATABASE_PORT,
                    REMOTE_DATABASE_USERS_NAME,
                    userDatabase
            );
            Log.d(TAG, "User database replicator was initialized");
        } catch (MalformedURLException e) {
            Log.d(TAG, "Failed to create replication for User database: " + e.getMessage());
            e.printStackTrace();
            userManager.close();
            return;
        }

        userDatabaseReplicator.startReplication();
        Log.d(TAG, "User database replication started");
    }

    private void startNotesDB() {
        try {
            manager = new Manager(
                    new AndroidContext(MyApplication.getInstance()),
                    Manager.DEFAULT_OPTIONS
            );
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Manager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            database = manager.getDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to open database: " + e.getMessage());
            e.printStackTrace();
            manager.close();
        }

        database.addChangeListener(event -> {
            Log.d(TAG, "Got change event on user notes database");

            for (DocumentChange change : event.getChanges()) {
                Log.d(TAG, "Change: " + change);

                com.couchbase.lite.Document doc = database.getExistingDocument(change.getDocumentId());
                Log.d(TAG, "Doc from db: " + doc);

                Map<String, Object> properties = null;
                ChangeListener.Event docEvent;

                if (doc == null) { // Doc is deleted
                    docEvent = ChangeListener.Event.DELETED;
                    Log.d(TAG, "Doc " + change.getDocumentId() + " is deleted");
                } else {
                    docEvent = ChangeListener.Event.UPDATED;
                    Log.d(TAG, "Doc prop: " + doc.getProperties());
                    properties = doc.getProperties();
                }

                Log.d(TAG, "Changed doc (JSON): " + properties);

                Log.d(TAG, "Amount of listeners: " + changeNoteListenerWeakRef.size());
                for (Integer key : changeNoteListenerWeakRef.keySet()) {
                    WeakReference<ChangeListener> reference = changeNoteListenerWeakRef.get(key);

                    ChangeListener listener = reference.get();
                    if (listener == null) {
                        Log.d(TAG, "Listener with hash: " + key + " is dead");
                        changeNoteListenerWeakRef.remove(key);
                        continue;
                    }
                    Log.d(TAG, "Listener with hash: " + key + " is alive");

                    listener.onChange(change.getDocumentId(), docEvent, properties);
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

        Log.d(TAG, "Database replication started");
    }

    public void startReplication(String userInfoGson) {
        currentUserData = new Gson().fromJson(userInfoGson, UserData.class);
        executor.submit(() -> {
            databaseReplicator.setPullUserIdFilter(currentUserData.getUsername());
            databaseReplicator.startReplication();
        });
    }

    public void resetDatabase() {
        executor.submit(() -> {
            try {
                notesQuery.stop();
                databaseReplicator.stopReplication();
                database.delete();
                startNotesDB();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Failed to delete CouchBase lite bases");
                e.printStackTrace();
            }
        });
    }

    public User getUser() {
        return new User(currentUserData);
    }

    public void addNote(NoteData noteData) {
        executor.submit(() -> {
            final com.couchbase.lite.Document doc = database.createDocument();
            UnsavedRevision unsavedRevision = doc.createRevision();

            try {
                Map<String, Object> noteMap = Util.objectToMap(noteData);
                unsavedRevision.setProperties(noteMap);
                unsavedRevision.save();

                Log.d(TAG, "Note in map: " + noteData.toString());

            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "ERROR - " + e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d(TAG, "Could not convert Note to Map: " + noteData.toString());
                e.printStackTrace();
            }

            Log.d(TAG, "Note has been added");
        });
    }

    public void addComment(CommentData commentData) {
        executor.submit(() -> {
            final com.couchbase.lite.Document doc = database.createDocument();
            UnsavedRevision unsavedRevision = doc.createRevision();

            try {
                Map<String, Object> noteMap = Util.objectToMap(commentData);
                unsavedRevision.setProperties(noteMap);
                unsavedRevision.save();

                Log.d(TAG, "Comment in map: " + commentData.toString());

            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "ERROR - " + e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d(TAG, "Could not convert Comment to Map: " + commentData.toString());
                e.printStackTrace();
            }

            Log.d(TAG, "Comment has been added");
        });
    }

    public void deleteNote(String noteId, Consumer<Boolean> consumer) {
        executor.submit(() -> {
            boolean res = false;
            com.couchbase.lite.Document doc = database.getExistingDocument(noteId);
            if (doc != null) {
                Log.d(TAG, "Start deleting note with id " + noteId + " ...");

                if (Objects.equals(doc.getProperties().get("type"), NOTE_TYPE)) {
                    List<CommentData> commentsList = getCommentsData(noteId);
                    for (CommentData commentData : commentsList) {
                        deleteDoc(commentData.get_id());
                    }
                }
                Log.d(TAG, "All comments was successfully deleted.");

                res = deleteDoc(noteId);
            } else {
                Log.d(TAG, "There is no such note in Database " + noteId);
            }
            consumer.accept(res);
        });
    }

    private boolean deleteDoc(String docId) {
        try {
            com.couchbase.lite.Document doc = database.getExistingDocument(docId);

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

    private NoteData getNoteData(String noteId) {
        com.couchbase.lite.Document doc = database.getExistingDocument(noteId);

        if (null != doc) {
            Log.d(TAG, "Document with id " + noteId + " was found.");
            return Util.convertToNote(doc.getProperties());
        }

        Log.d(TAG, "There is no such Note in Database + " + noteId);
        return null;
    }

    public void getNotes(Consumer<List<Card>> consumer) {
        executor.submit(() -> {
            Log.d(TAG, "Load User notes:");
            List<Card> cards = new ArrayList<>();

            Log.d(TAG, "Before query request");
            QueryEnumerator qr = notesQuery.getRows();
            Log.d(TAG, "Get query enumerator");

            for (QueryRow row : qr) {
                Map<String, Object> noteProperties = row.getDocument().getProperties();
                Log.d(TAG, "type is: " + noteProperties.get("type"));

                NoteData noteData = Util.convertToNote(noteProperties);
                cards.add(new Card(noteData, currentUserData));
            }
            Log.d(TAG, "After query request");
            Log.d(TAG, "notes: " + cards.size());

            consumer.accept(cards);
        });
    }

    private List<CommentData> getCommentsData(String noteId) {
        List<CommentData> commentData = new ArrayList<>();

        Log.d(TAG, "Load User comments: ");

        for (QueryRow row : commentQuery.getRows()) {
            Map<String, Object> commentProperties = row.getDocument().getProperties();
            Log.d(TAG, "type is: " + commentProperties.get("type"));
            if (noteId.equals(commentProperties.get("noteId"))) {
                commentData.add(Util.convertToComment(commentProperties));
            }
        }

        Log.d(TAG, "comments: " + commentData.size());
        return commentData;
    }

    public void getFriends(Consumer<List<User>> consumer) {
        executor.submit(() -> {
            List<User> users = getFriends();
            consumer.accept(users);
        });
    }

    private List<User> getFriends() {
        List<String> friendsId = currentUserData.getFriends();
        List<User> friends = new ArrayList<>();

        for (int i = 0; i < friendsId.size(); i++) {
            String userId = friendsId.get(i);
            UserData userData = getUserData(userId);
            if (userData != null) {
                friends.add(new User(userData));
            }
        }
        return friends;
    }

    public void getUserInfoGson(String username, Consumer<String> consumer) {
        executor.submit(() -> {
            UserData userData = getUserData(username);
            String json = new Gson().toJson(userData);
            consumer.accept(json);
        });
    }

    public void getUser(String username, Consumer<User> consumer) {
        executor.submit(() -> {
            UserData userData = getUserData(username);
            User user = null;
            if (userData != null) {
                user = new User(userData);
            }
            consumer.accept(user);
        });
    }

    private UserData getUserData(String username) {
        com.couchbase.lite.Document doc = userDatabase.getExistingDocument(username);

        if (null != doc) {
            Log.d(TAG, "Document with id " + username + " was found.");
            return Util.convertToUser(doc.getProperties());
        }

        Log.d(TAG, "There is no such Note in Database + " + username);
        return null;
    }

    public void tryToAddFriend(String username, Consumer<FriendsViewModel.ResultFriendAddition> consumer) {
        executor.submit(() -> {
            if (currentUserData.checkIfFriendAdded(username)) {
                Log.d(TAG, "User with username " + username + " already added to list of friends");
                consumer.accept(FriendsViewModel.ResultFriendAddition.ALREADY_ADDED);
                return;
            }

            if (userDatabase.getExistingDocument(username) == null) {
                Log.d(TAG, "Can't find user with username " + username + ". Doc wasn't found.");
                consumer.accept(FriendsViewModel.ResultFriendAddition.DOESNT_EXIT);
                return;
            }

            com.couchbase.lite.Document doc = userDatabase.getExistingDocument(currentUserData.getUsername());
            if (doc != null) {
                Log.d(TAG, "Document with id " + currentUserData.getUsername() + " was found.");

                currentUserData.addFriend(username);
                final Map<String, Object> docUserProperties;
                try {
                    docUserProperties = Util.objectToMap(currentUserData);
                    Log.d(TAG, "Convert User to Map: " + docUserProperties);
                } catch (JSONException e) {
                    Log.d(TAG, "Could not convert User to Map: " + currentUserData.toString());
                    consumer.accept(FriendsViewModel.ResultFriendAddition.ERROR);
                    return;
                }

                try {
                    doc.update(newRevision -> {
                        Log.d(TAG, "update");
                        newRevision.setUserProperties(docUserProperties);
                        return true;
                    });
                    consumer.accept(FriendsViewModel.ResultFriendAddition.SUCCESS);
                } catch (CouchbaseLiteException e) {
                    Log.d(TAG, "CouchbaseLite Error!");
                    consumer.accept(FriendsViewModel.ResultFriendAddition.ERROR);
                }
            } else {
                Log.d(TAG, "There is no such user in database + " + currentUserData.getUsername());
                consumer.accept(FriendsViewModel.ResultFriendAddition.DOESNT_EXIT);
            }
        });
    }

    public void getDocument(String noteId, Consumer<Document> consumer) {
        executor.submit(() -> {
            NoteData noteData = getNoteData(noteId);
            if (noteData == null) {
                Log.d(TAG, "Note with id " + noteId + " wasn't found");
                return;
            }

            Map<CommentData, UserData> userComments = new HashMap<>();
            List<CommentData> commentData = getCommentsData(noteData.get_id());
            for (CommentData comment : commentData) {
                UserData userData = getUserData(comment.getUserId());
                userComments.put(comment, userData);
            }

            consumer.accept(new Document(currentUserData, noteData, userComments));
        });
        Log.d(TAG, "Finish getting note with id: " + noteId);
    }
}
