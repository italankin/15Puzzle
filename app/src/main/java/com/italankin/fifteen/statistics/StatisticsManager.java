package com.italankin.fifteen.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsManager {

    public static StatisticsManager INSTANCE = new StatisticsManager();

    private final Map<Key, List<Entry>> records = new HashMap<>(100);

    private StatisticsManager() {
    }

    public void add(int width, int height, int type, boolean hard, long time, int moves) {
        Key key = new Key(width, height, type, hard);
        Entry entry = new Entry(time, moves);
        List<Entry> entries = records.get(key);
        if (entries == null) {
            entries = new ArrayList<>(100);
            records.put(key, entries);
        }
        entries.add(entry);
    }

    public Statistics get(int width, int height, int type, boolean hard) {
        if (records.isEmpty()) {
            return Statistics.EMPTY;
        }
        Key key = new Key(width, height, type, hard);
        List<Entry> entries = records.get(key);
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

    public void clear(int width, int height, int type, boolean hard) {
        Key key = new Key(width, height, type, hard);
        records.remove(key);
    }

    public void clear() {
        records.clear();
    }

    private static Statistics.Avg avgSingle(List<Entry> entries) {
        int size = entries.size();
        if (size < 1) {
            return null;
        }
        Entry last = entries.get(size - 1);
        return new Statistics.Avg(last.time, last.moves, last.tps);
    }

    private static Statistics.Avg avg(List<Entry> entries, int num) {
        int size = entries.size();
        if (size < num || num < 3) {
            return null;
        }
        List<Entry> lastN;
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

    private static long avgTime(List<Entry> entries) {
        Collections.sort(entries, Entry.BY_TIME);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        long total = 0;
        for (Entry entry : entries) {
            total += entry.time;
        }
        return total / entries.size();
    }

    private static float avgMoves(List<Entry> entries) {
        Collections.sort(entries, Entry.BY_MOVES);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        float total = 0;
        for (Entry entry : entries) {
            total += entry.moves;
        }
        return total / entries.size();
    }

    private static float avgTps(List<Entry> entries) {
        Collections.sort(entries, Entry.BY_TPS);
        int size = entries.size();
        if (size > 2) {
            entries.remove(size - 1);
            entries.remove(0);
        }
        float total = 0;
        for (Entry entry : entries) {
            total += entry.tps;
        }
        return total / entries.size();
    }

    private static class Key {

        final int width;
        final int height;
        final int type;
        final boolean hard;

        private Key(int width, int height, int type, boolean hard) {
            this.width = width;
            this.height = height;
            this.type = type;
            this.hard = hard;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key) o;
            if (width != key.width) {
                return false;
            }
            if (height != key.height) {
                return false;
            }
            if (type != key.type) {
                return false;
            }
            return hard == key.hard;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + type;
            result = 31 * result + (hard ? 1 : 0);
            return result;
        }
    }

    private static class Entry {

        static final Comparator<Entry> BY_TIME = (lhs, rhs) -> {
            return Long.compare(lhs.time, rhs.time);
        };
        static final Comparator<Entry> BY_MOVES = (lhs, rhs) -> {
            return Long.compare(lhs.moves, rhs.moves);
        };
        static final Comparator<Entry> BY_TPS = (lhs, rhs) -> {
            return Float.compare(lhs.tps, rhs.tps);
        };

        final long time;
        final int moves;
        final float tps;

        private Entry(long time, int moves) {
            this.time = time;
            this.moves = moves;
            this.tps = (float) moves / (float) time * 1000f;
        }
    }
}
