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

import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;
import ru.kudasheva.noteskeeper.data.models.UsersBase;

public class RemoteDataRepository implements DataRepository {
    private final static String TAG = RemoteDataRepository.class.getSimpleName();

    @Override
    public void initDatabase(Context context, User user) {
        DatabaseManager dbManager = DatabaseManager.getSharedInstance();
        dbManager.initCouchbaseLite(context);
        dbManager.openOrCreateDatabaseForUser(context, user);
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
        Document doc = database.getDocument(getUsername());

        if (doc == null) {
            Log.d(TAG, "User doesn't exist");
            return false;
        }

        List<String> friendsList = Util.convertFriends(doc.toJSON());
        if (friendsList != null && friendsList.contains(friendName)) {
            return true;
        }

        MutableDocument mutableDoc = doc.toMutable();
        MutableArray friendsId = mutableDoc.getArray("friendsId");
        if (friendsId == null) {
            friendsId = new MutableArray();
        }
        friendsId.addString(friendName);

        mutableDoc.setArray("friendsId", friendsId);

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

        UsersBase users = Util.convertUsers(doc.toJSON());
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
        return DatabaseManager.getSharedInstance().getCurrentUsername();
    }

    @Override
    public List<String> getFriends() {
        Database database = DatabaseManager.getDatabase();
        Document doc = database.getDocument(getUsername());

        if (doc == null) {
            Log.d(TAG, "User doesn't exist");
            return null;
        }

        return Util.convertFriends(doc.toJSON());
    }

}
