package com.italankin.fifteen.statistics;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        Statistics.Avg[] byTime = bestWorstBy(entries, StatisticsEntry.BY_TIME);
        Statistics.Avg[] byMoves = bestWorstBy(entries, StatisticsEntry.BY_MOVES);
        return new Statistics(entries.size(),
                single,
                ao5,
                ao12,
                ao50,
                ao100,
                session,
                byTime[0],
                byMoves[0],
                byTime[1],
                byMoves[1]);
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
            lastN = new ArrayList<>(entries);
        } else {
            // take last 'num' entries
            lastN = new ArrayList<>(entries.subList(size - num, size));
        }
        Collections.sort(lastN, StatisticsEntry.BY_TIME);
        int lastNSize = lastN.size();
        if (lastNSize > 2) {
            lastN.remove(lastNSize - 1);
            lastN.remove(0);
        }
        long time = avgTime(lastN);
        float moves = avgMoves(lastN);
        float tps = avgTps(lastN);
        return new Statistics.Avg(time, moves, tps);
    }

    /**
     * @return an array {@code [best, worst]} by a given {@code comparator}
     */
    private static Statistics.Avg[] bestWorstBy(
            List<StatisticsEntry> entries,
            Comparator<StatisticsEntry> comparator) {
        int size = entries.size();
        switch (size) {
            case 0:
                return new Statistics.Avg[]{null, null};
            case 1:
                StatisticsEntry entry = entries.get(0);
                Statistics.Avg avg = new Statistics.Avg(entry.time, entry.moves, entry.tps);
                return new Statistics.Avg[]{avg, avg};
            default:
                Collections.sort(entries, comparator);
                StatisticsEntry best = entries.get(0);
                StatisticsEntry worst = entries.get(entries.size() - 1);
                return new Statistics.Avg[]{
                        new Statistics.Avg(best.time, best.moves, best.tps),
                        new Statistics.Avg(worst.time, worst.moves, worst.tps)
                };
        }
    }

    private static long avgTime(List<StatisticsEntry> entries) {
        long total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.time;
        }
        return total / entries.size();
    }

    private static float avgMoves(List<StatisticsEntry> entries) {
        float total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.moves;
        }
        return total / entries.size();
    }

    private static float avgTps(List<StatisticsEntry> entries) {
        float total = 0;
        for (StatisticsEntry entry : entries) {
            total += entry.tps;
        }
        return total / entries.size();
    }
}
