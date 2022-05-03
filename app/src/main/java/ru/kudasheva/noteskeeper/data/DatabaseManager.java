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
    private final static String dbName = "userprofile";

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
        config.setDirectory(String.format("%s/%s", context.getFilesDir(), username));

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
                    Log.d(TAG, "To JSON: " + doc.toJSON());
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
                database.close();
                database = null;

                Log.d(TAG, "Database for user " + currentUser + " was created and opened");
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void deregisterForDatabaseChanges() {
        database.removeChangeListener(listenerToken);
    }
}
