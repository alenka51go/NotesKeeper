package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

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
import java.util.concurrent.Future;
import java.util.function.Consumer;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.models.Comment;
import ru.kudasheva.noteskeeper.data.models.DatabaseReplicator;
import ru.kudasheva.noteskeeper.data.models.FullNoteData;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;
import ru.kudasheva.noteskeeper.friends.FriendInfoCard;
import ru.kudasheva.noteskeeper.friends.FriendsViewModel;
import ru.kudasheva.noteskeeper.notescroll.NoteShortCard;

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

    private User currentUser;

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

                Document doc = database.getExistingDocument(change.getDocumentId());
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

    public void startReplication(User user) {
        currentUser = user;
        executor.submit(() -> {
            databaseReplicator.setPullUserIdFilter(currentUser.getUsername());
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

    public String getUsername() {
        return currentUser.getUsername();
    }

    public String getFullUsername() {
        return currentUser.getFullUsername();
    }

    public void addNote(Note note) {
        executor.submit(() -> {
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
        });
    }

    public void addComment(Comment comment) {
        executor.submit(() -> {
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
        });
    }

    public void deleteNote(String noteId, Consumer<Boolean> consumer) {
        executor.submit(() -> {
            boolean res = false;
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

                res = deleteDoc(noteId);
            } else {
                Log.d(TAG, "There is no such note in Database " + noteId);
            }
            consumer.accept(res);
        });
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

    private Note getNote(String noteId) {
        Document doc = database.getExistingDocument(noteId);

        if (null != doc) {
            Log.d(TAG, "Document with id " + noteId + " was found.");
            return Util.convertToNote(doc.getProperties());
        }

        Log.d(TAG, "There is no such Note in Database + " + noteId);
        return null;
    }

    public void getNoteShortCards(Consumer<List<NoteShortCard>> consumer) {
        executor.submit(() -> {
            Log.d(TAG, "Load User notes:");
            List<NoteShortCard> shortNotes = new ArrayList<>();

            Log.d(TAG, "Before query request");
            QueryEnumerator qr = notesQuery.getRows();
            Log.d(TAG, "Get query enumerator");

            for (QueryRow row : qr) {
                Map<String, Object> noteProperties = row.getDocument().getProperties();
                Log.d(TAG, "type is: " + noteProperties.get("type"));

                NoteShortCard shortNote = new NoteShortCard(Util.convertToNote(noteProperties));
                shortNotes.add(shortNote);
            }
            Log.d(TAG, "After query request");
            Log.d(TAG, "notes: " + shortNotes.size());

            consumer.accept(shortNotes);
        });
    }

    private List<Comment> getComments(String noteId) {
        List<Comment> comments = new ArrayList<>();

        Log.d(TAG, "Load User comments: ");

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

    public void getFriendsInfoCard(Consumer<List<FriendInfoCard>> consumer) {
        executor.submit(() -> {
            List<User> friends = getFriends();
            List<FriendInfoCard> friendInfoCards = new ArrayList<>();

            for (User user : friends) {
                FriendInfoCard friendInfoCard = new FriendInfoCard(user.getFullUsername());
                friendInfoCards.add(friendInfoCard);
            }

            consumer.accept(friendInfoCards);
        });
    }

    public void getFriends(Consumer<List<User>> consumer) {
        executor.submit(() -> {
            List<User> users = getFriends();
            consumer.accept(users);
        });
    }

    private List<User> getFriends() {
        List<String> friendsId = currentUser.getFriends();
        List<User> friends = new ArrayList<>();

        for (int i = 0; i < friendsId.size(); i++) {
            String userId = friendsId.get(i);
            friends.add(getUser(userId));
        }
        return friends;
    }

    public void getUser(String username, Consumer<User> consumer) {
        executor.submit(() -> {
            User user = getUser(username);
            consumer.accept(user);
        });
    }

    private User getUser(String username) {
        Document doc = userDatabase.getExistingDocument(username);

        if (null != doc) {
            Log.d(TAG, "Document with id " + username + " was found.");
            return Util.convertToUser(doc.getProperties());
        }

        Log.d(TAG, "There is no such Note in Database + " + username);
        return null;
    }

    public void tryToAddFriend(String username, Consumer<FriendsViewModel.ResultFriendAddition> consumer) {
        executor.submit(() -> {
            if (currentUser.checkIfFriendAdded(username)) {
                Log.d(TAG, "User with username " + username + " already added to list of friends");
                consumer.accept(FriendsViewModel.ResultFriendAddition.ALREADY_ADDED);
                return;
            }

            if (userDatabase.getExistingDocument(username) == null) {
                Log.d(TAG, "Can't find user with username " + username + ". Doc wasn't found.");
                consumer.accept(FriendsViewModel.ResultFriendAddition.DOESNT_EXIT);
                return;
            }

            Document doc = userDatabase.getExistingDocument(currentUser.getUsername());
            if (doc != null) {
                Log.d(TAG, "Document with id " + currentUser.getUsername() + " was found.");

                currentUser.addFriend(username);
                final Map<String, Object> docUserProperties;
                try {
                    docUserProperties = Util.objectToMap(currentUser);
                    Log.d(TAG, "Convert User to Map: " + docUserProperties);
                } catch (JSONException e) {
                    Log.d(TAG, "Could not convert User to Map: " + currentUser.toString());
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
                Log.d(TAG, "There is no such user in database + " + currentUser.getUsername());
                consumer.accept(FriendsViewModel.ResultFriendAddition.DOESNT_EXIT);
            }
        });
    }

    public void getFullNoteData(String noteId, Consumer<FullNoteData> consumer) {
        executor.submit(() -> {
            Note note = getNote(noteId);
            if (note == null) {
                Log.d(TAG, "Note with id " + noteId + " wasn't found");
                return;
            }

            Map<Comment, User> userComments = new HashMap<>();
            List<Comment> comments = getComments(note.get_id());
            for (Comment comment : comments) {
                User user = getUser(comment.getUserId());
                userComments.put(comment, user);
            }

            consumer.accept(new FullNoteData(currentUser, note, userComments));
        });
        Log.d(TAG, "Finish getting note with id: " + noteId);
    }
}
