package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseReplicator {
    private static final String TAG = DatabaseReplicator.class.getSimpleName();

    private static final String CLIENT_USERNAME = "mobileclient";
    private static final String CLIENT_PASSWORD = "2Mu8UxlRn1";

    private final Replication mPushReplication;
    private final Replication mPullReplication;

    private final PushChangeListener mPushChangeListener;
    private final PullChangeListener mPullChangeListener;

    public DatabaseReplicator(String ipAddress, int port, String dbName, Database database) throws MalformedURLException {
        mPushChangeListener = new PushChangeListener();
        mPullChangeListener = new PullChangeListener();

        String stringUrl = "http://" + CLIENT_USERNAME + ":" + CLIENT_PASSWORD + "@"+ ipAddress + ":" + port + "/" + dbName + "/";
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG,"Unable to parse remote database address: " + stringUrl);
            throw e;
        }

        mPushReplication = database.createPushReplication(url);
        mPullReplication = database.createPullReplication(url);

        mPushReplication.addChangeListener(mPushChangeListener);
        mPullReplication.addChangeListener(mPullChangeListener);
    }

    public void startReplication() {
        Log.d(TAG, "startReplication()");

        mPushReplication.setContinuous(true);
        mPushReplication.start();

        mPullReplication.setContinuous(true);
        mPullReplication.start();
    }

    public void stopReplication() {
        mPushReplication.stop();
        mPushReplication.removeChangeListener(mPushChangeListener);

        mPullReplication.stop();
        mPullReplication.removeChangeListener(mPullChangeListener);
    }

    public void setPullUserIdFilter(String userId) {
        mPullReplication.setFilter("_selector");

        Map<String, Object> inKeyValueMap = new HashMap<>();
        inKeyValueMap.put("$in", Arrays.asList(userId));

        Map<String, Object> helper = new HashMap<>();
        helper.put("userId", userId);
        helper.put("sharedUsers", inKeyValueMap);
        helper.put("_deleted", true);

        List<Object> orKeyValueList = new ArrayList<>(helper.entrySet());

        Map<String, Object> keyValue = new HashMap<>();
        keyValue.put("$or", orKeyValueList);

        Map<String, Object> params =  new HashMap<>();
        params.put("selector", keyValue);

        Log.d(TAG, params.toString());

        mPullReplication.setFilterParams(params);
    }

    private static class PushChangeListener implements Replication.ChangeListener {
        @Override
        public void changed(Replication.ChangeEvent event) {
            Replication replication = event.getSource();

            Log.d(TAG, "Push replication changed. Status: " + replication.getStatus());

            if (!replication.isRunning()) {
                String msg = String.format("Push replicator %s not running", replication);

                Log.d(TAG, msg);
            } else {
                int processed = replication.getCompletedChangesCount();
                int total = replication.getChangesCount();

                Log.d(TAG, String.format("Push replicator processed %d / %d", processed, total));
            }
        }
    }

    private static class PullChangeListener implements Replication.ChangeListener {
        @Override
        public void changed(Replication.ChangeEvent event) {
            Replication replication = event.getSource();

            Log.d(TAG, "Pull replication changed. Status: " + replication.getStatus());

            if (!replication.isRunning()) {
                String msg = String.format("Pull replicator %s not running", replication);

                Log.d(TAG, msg);
            } else {
                int processed = replication.getCompletedChangesCount();
                int total = replication.getChangesCount();

                Log.d(TAG, String.format("Pull replicator processed %d / %d", processed, total));
            }
        }
    }
}
