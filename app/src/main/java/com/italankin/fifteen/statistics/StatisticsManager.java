package com.italankin.fifteen.statistics;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManager {

    public synchronized static StatisticsManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StatisticsManager(context);
            INSTANCE.loadSession();
        }
        return INSTANCE;
    }

    private static StatisticsManager INSTANCE;

    private final SessionStorage sessionStorage;
    private final Map<StatisticsKey, List<StatisticsEntry>> records = new ConcurrentHashMap<>(50);

    private StatisticsManager(Context context) {
        sessionStorage = new SessionStorage(context);
    }

    public void add(int width, int height, int type, boolean hard, long time, int moves) {
        StatisticsKey key = new StatisticsKey(width, height, type, hard);
        StatisticsEntry entry = new StatisticsEntry(time, moves);
        List<StatisticsEntry> entries = records.get(key);
        if (entries == null) {
            entries = new ArrayList<>(100);
            records.put(key, entries);
        }
        entries.add(entry);
        sessionStorage.write(records);
    }

    public Statistics get(int width, int height, int type, boolean hard) {
        if (records.isEmpty()) {
            return Statistics.EMPTY;
        }
        StatisticsKey key = new StatisticsKey(width, height, type, hard);
        List<StatisticsEntry> entries = records.get(key);
        if (entries == null || entries.isEmpty()) {
            return Statistics.EMPTY;
        }
        Statistics.Avg single = avgSingle(entries);
        Statistics.Avg ao5 = avg(entries, 5);
        Statistics.Avg ao12 = avg(entries, 12);
        Statistics.Avg ao50 = avg(entries, 50);
        Statistics.Avg ao100 = avg(entries, 100);
        Statistics.Avg session = avg(entries, entries.size());
        return new Statistics(entries.size(), single, ao5, ao12, ao50, ao100, session);
    }

    public Map<StatisticsKey, List<StatisticsEntry>> getAll() {
        return Collections.unmodifiableMap(records);
    }

    public void clear() {
        records.clear();
        sessionStorage.clear();
    }

    private void loadSession() {
        sessionStorage.read(records::putAll);
    }

    private static Statistics.Avg avgSingle(List<StatisticsEntry> entries) {
        int size = entries.size();
        if (size < 1) {
            return null;
        }
        StatisticsEntry last = entries.get(size - 1);
        return new Statistics.Avg(last.time, last.moves, last.tps);
    }

    private static Statistics.Avg avg(List<StatisticsEntry> entries, int num) {
        int size = entries.size();
        if (size < num || num < 3) {
            return null;
        }
        List<StatisticsEntry> lastN;
        if (size == num) {
            lastN = entries;
        } else {
            // take last 'num' entries
            lastN = entries.subList(size - num, size);
        }
        long time = avgTime(new ArrayList<>(lastN));
        float moves = avgMoves(new ArrayList<>(lastN));
        float tps = avgTps(new ArrayList<>(lastN));
        return new Statistics.Avg(time, moves, tps);
    }

    private static long avgTime(List<StatisticsEntry> entries) {
        Collections.sort(entries, StatisticsEntry.BY_TIME);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        long total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.time;
        }
        return total / entries.size();
    }

    private static float avgMoves(List<StatisticsEntry> entries) {
        Collections.sort(entries, StatisticsEntry.BY_MOVES);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        float total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.moves;
        }
        return total / entries.size();
    }

    private static float avgTps(List<StatisticsEntry> entries) {
        Collections.sort(entries, StatisticsEntry.BY_TPS);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        float total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.tps;
        }
        return total / entries.size();
    }
}
