package com.italankin.fifteen.statistics;

import android.content.Context;

import com.italankin.fifteen.BuildConfig;
import com.italankin.fifteen.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class SessionStorage {

    static final String KEY_WIDTH = "width";
    static final String KEY_HEIGHT = "height";
    static final String KEY_TYPE = "type";
    static final String KEY_HARD = "hard";
    static final String KEY_ENTRIES = "entries";
    static final String KEY_TIME = "time";
    static final String KEY_MOVES = "moves";

    static final int BUFFER_SIZE = 8192;
    static final int JSON_INDENT = 2;

    private static final String SESSION_FILENAME = "session.json";

    private final File sessionFile;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    SessionStorage(Context context) {
        this.sessionFile = new File(context.getFilesDir(), SESSION_FILENAME);
    }

    void write(Map<StatisticsKey, List<StatisticsEntry>> session) {
        executorService.submit(new WriteSession(sessionFile, Collections.unmodifiableMap(session)));
    }

    void read(OnSessionLoaded action) {
        if (!sessionFile.exists()) {
            Logger.d("No saved session");
            return;
        }
        Future<Map<StatisticsKey, List<StatisticsEntry>>> task = executorService.submit(new ReadSession(sessionFile));
        executorService.submit(() -> {
            try {
                Map<StatisticsKey, List<StatisticsEntry>> result = task.get(15, TimeUnit.SECONDS);
                action.onSessionLoaded(result);
            } catch (Exception e) {
                Logger.e(e, "Failed to load session");
            }
        });
    }

    void clear() {
        if (sessionFile.delete()) {
            Logger.d("Cleared session");
        }
    }

    interface OnSessionLoaded {
        void onSessionLoaded(Map<StatisticsKey, List<StatisticsEntry>> session);
    }
}

class WriteSession implements Runnable {

    private final File output;
    private final Map<StatisticsKey, List<StatisticsEntry>> records;

    WriteSession(File output, Map<StatisticsKey, List<StatisticsEntry>> records) {
        this.output = output;
        this.records = records;
    }

    @Override
    public void run() {
        try {
            JSONArray keysArray = new JSONArray();
            for (Map.Entry<StatisticsKey, List<StatisticsEntry>> entry : records.entrySet()) {
                JSONObject node = toJson(entry.getKey(), entry.getValue());
                keysArray.put(node);
            }
            String json = BuildConfig.DEBUG ? keysArray.toString(SessionStorage.JSON_INDENT) : keysArray.toString();
            try (FileWriter writer = new FileWriter(output)) {
                writer.write(json);
            }
            Logger.d("Wrote session: %d chars", json.length());
        } catch (Exception e) {
            Logger.e(e, "Cannot write session: %s", e.getMessage());
        }
    }

    private static JSONObject toJson(StatisticsKey key, List<StatisticsEntry> entries) throws JSONException {
        JSONArray array = new JSONArray();
        for (StatisticsEntry entry : entries) {
            JSONObject node = new JSONObject()
                    .put(SessionStorage.KEY_TIME, entry.time)
                    .put(SessionStorage.KEY_MOVES, entry.moves);
            array.put(node);
        }
        return new JSONObject()
                .put(SessionStorage.KEY_WIDTH, key.width)
                .put(SessionStorage.KEY_HEIGHT, key.height)
                .put(SessionStorage.KEY_TYPE, key.type)
                .put(SessionStorage.KEY_HARD, key.hard)
                .put(SessionStorage.KEY_ENTRIES, array);
    }
}

class ReadSession implements Callable<Map<StatisticsKey, List<StatisticsEntry>>> {

    private final File input;

    ReadSession(File input) {
        this.input = input;
    }

    @Override
    public Map<StatisticsKey, List<StatisticsEntry>> call() {
        try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
            StringBuilder sb = new StringBuilder((int) input.length());
            char[] buff = new char[SessionStorage.BUFFER_SIZE];
            int read;
            while ((read = reader.read(buff)) != -1) {
                sb.append(buff, 0, read);
            }
            Map<StatisticsKey, List<StatisticsEntry>> result = fromJson(sb);
            Logger.d("Read session: %s", result);
            return result;
        } catch (Exception e) {
            Logger.e(e, "Cannot read session: %s", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private static Map<StatisticsKey, List<StatisticsEntry>> fromJson(StringBuilder sb) throws JSONException {
        JSONArray keysArray = new JSONArray(sb.toString());
        int keysArraySize = keysArray.length();
        Map<StatisticsKey, List<StatisticsEntry>> result = new HashMap<>(keysArraySize);
        for (int i = 0; i < keysArraySize; i++) {
            JSONObject keyNode = keysArray.getJSONObject(i);
            StatisticsKey key = keyFromJson(keyNode);
            JSONArray entriesArray = keyNode.getJSONArray(SessionStorage.KEY_ENTRIES);
            List<StatisticsEntry> entries = entriesFromJson(entriesArray);
            result.put(key, entries);
        }
        return result;
    }

    private static List<StatisticsEntry> entriesFromJson(JSONArray entriesArray) throws JSONException {
        int entriesArraySize = entriesArray.length();
        List<StatisticsEntry> entries = new ArrayList<>(entriesArraySize);
        for (int j = 0; j < entriesArraySize; j++) {
            JSONObject node = entriesArray.getJSONObject(j);
            long time = node.getLong(SessionStorage.KEY_TIME);
            int moves = node.getInt(SessionStorage.KEY_MOVES);
            entries.add(new StatisticsEntry(time, moves));
        }
        return entries;
    }

    private static StatisticsKey keyFromJson(JSONObject keyNode) throws JSONException {
        int width = keyNode.getInt(SessionStorage.KEY_WIDTH);
        int height = keyNode.getInt(SessionStorage.KEY_HEIGHT);
        int type = keyNode.getInt(SessionStorage.KEY_TYPE);
        boolean hard = keyNode.getBoolean(SessionStorage.KEY_HARD);
        return new StatisticsKey(width, height, type, hard);
    }
}

