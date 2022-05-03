package ru.kudasheva.noteskeeper.data;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;

public class DatabaseManager {
    private final static String TAG = DatabaseManager.class.getSimpleName();
    private final static String dbName = "noteskeeper";

    private static Database database;
    private static DatabaseManager instance;

    private ListenerToken listenerToken;
    private String currentUser;

    public static DatabaseManager getSharedInstance() {
        if (instance == null) {
            instance = new DatabaseManager();

            Log.d(TAG, "DatabaseManager created");
        }
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
    }

    public void openOrCreateDatabaseForUser(Context context, String username) {
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(String.format("%s/%s", context.getFilesDir(), "user"));

        try {
            database = new Database(dbName, config);
            registerForDataBaseChanges();
            currentUser = username;

            Log.d(TAG, "Database for user " + username + " was created or opened");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void registerForDataBaseChanges() {
        listenerToken = database.addChangeListener(change -> {
            for (String docId : change.getDocumentIDs()) {
                Document doc = database.getDocument(docId);
                if (doc != null) {
                    Log.d(TAG, "Document was added/updated");
                }
                else {
                    Log.d(TAG,"Document was deleted");
                }
            }
        });
    }

    public void closeDatabase() {
        try {
            if (database != null) {
                deregisterForDatabaseChanges();
                database.delete();
                database.close();
                database = null;

                Log.d(TAG, "delete all documents: success");
                Log.d(TAG, "Database for user " + currentUser + " was closed");
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d(TAG, "delete all documents: Failed to close and delete database.");
        }
    }

    private void deregisterForDatabaseChanges() {
        database.removeChangeListener(listenerToken);
    }
}
