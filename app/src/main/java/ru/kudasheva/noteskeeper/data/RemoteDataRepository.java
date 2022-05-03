package ru.kudasheva.noteskeeper.data;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.kudasheva.noteskeeper.data.models.Friends;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.Users;

public class RemoteDataRepository implements DataRepository {
    private final static String TAG = RemoteDataRepository.class.getSimpleName();

    @Override
    public void initDatabase(Context context, String username) {
        DatabaseManager dbManager = DatabaseManager.getSharedInstance();
        dbManager.initCouchbaseLite(context);
        dbManager.openOrCreateDatabaseForUser(context, username);
    }

    @Override
    public void closeDatabase() {
        DatabaseManager.getSharedInstance().closeDatabase();
    }

    @Override
    public void addNote(Map<String, Object> info) {
        Database database = DatabaseManager.getDatabase();

        MutableDocument mutableDocument = new MutableDocument(info);
        mutableDocument.setValue("type", "note");

        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addNewFriend(String friendName) {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument(getUsername() + "friends");

        if (doc == null) {
            Log.d(TAG, "File with friends hasn't been upload");
            return false;
        }

        Friends friends = Util.convertFriends(doc.toJSON());
        List<String> friendsList = friends.getAllFriends();
        if (friendsList != null && friendsList.contains(friendName)) {
            return true;
        }

        MutableDocument mutableDoc = doc.toMutable();
        MutableArray friendsNames = mutableDoc.getArray("friends");
        if (friendsNames == null) {
            friendsNames = new MutableArray();
        }
        friendsNames.addString(friendName);

        mutableDoc.setArray("friends", friendsNames);

        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean addComment(String docId, Map<String, Object> info) {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument(docId);

        if (doc == null) {
            Log.d(TAG, "Doc with id:" + docId + " doesn't exist");
            return false;
        }

        MutableDocument mutableDoc = doc.toMutable();
        MutableArray comments = mutableDoc.getArray("comments");

        if (comments == null) {
            comments = new MutableArray();
        }
        Dictionary dict = Util.createDictionary(info);
        comments.addDictionary(dict);

        mutableDoc.setArray("comments", comments);

        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Note getNoteById(String docId) {
        Database database = DatabaseManager.getDatabase();

        Document doc =  database.getDocument(docId);
        if (doc != null) {
            return Util.convertNote(doc.toJSON());
        }
        return null;
    }

    @Override
    public List<Note> getAllNotes() {
        Database database = DatabaseManager.getDatabase();
        List<Note> noteList = new ArrayList<>();
        
        // создаем запрос, чтобы собрать все заметки, которые лежат в базе пользователя
        Query query = QueryBuilder
                .select(SelectResult.expression(Meta.id),
                        SelectResult.expression(Meta.revisionID),
                        SelectResult.property("username"),
                        SelectResult.property("title"),
                        SelectResult.property("text"),
                        SelectResult.property("date"),
                        SelectResult.property("friends"),
                        SelectResult.property("comments")
                )
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("note")));

        try{
            ResultSet resSet = query.execute();
            for (Result result : resSet) {
                Log.d(TAG, "Res JSON: " + result.toJSON());
                Note note = Util.convertNote(result.toJSON());
                assert (note != null);
                noteList.add(note);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return noteList;
    }

    @Override
    public boolean checkIfUserExist(String name) {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument("users");

        if (doc == null) {
            Log.d(TAG, "File with users hasn't been upload");
            return false;
        }

        Users users = Util.convertUsers(doc.toJSON());
        return users.contains(name);
    }

    @Override
    public boolean deleteNote(String docId) {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument(docId);

        if (doc == null) {
            Log.d(TAG, "Document with id: " + docId + " doesn't contains in base");
            return false;
        }

        try {
            database.delete(doc);
        } catch (CouchbaseLiteException e) {
            Log.d(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public String getUsername() {
        return DatabaseManager.getSharedInstance().getCurrentUser();
    }

    @Override
    public List<String> getFriends() {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument(getUsername() + "friends");

        if (doc == null) {
            Log.d(TAG, "File with friends hasn't been upload");
            return null;
        }

        Friends friends = Util.convertFriends(doc.toJSON());
        return friends.getAllFriends();
    }

}
