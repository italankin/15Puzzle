package com.italankin.fifteen.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsManager {

    public static StatisticsManager INSTANCE = new StatisticsManager();

    private final Map<Key, List<Entry>> records = new HashMap<>(100);

    private StatisticsManager() {
    }

    public void add(int width, int height, int mode, boolean hard, long time, int moves) {
        Key key = new Key(width, height, mode, hard);
        Entry entry = new Entry(time, moves);
        List<Entry> entries = records.get(key);
        if (entries == null) {
            entries = new ArrayList<>(100);
            records.put(key, entries);
        }
        entries.add(entry);
    }

    public Statistics get(int width, int height, int mode, boolean hard) {
        if (records.isEmpty()) {
            return Statistics.EMPTY;
        }
        Key key = new Key(width, height, mode, hard);
        List<Entry> entries = records.get(key);
        if (entries == null || entries.isEmpty()) {
            return Statistics.EMPTY;
        }
        Statistics.Avg ao5 = avg(entries, 5);
        Statistics.Avg ao12 = avg(entries, 12);
        Statistics.Avg ao50 = avg(entries, 50);
        Statistics.Avg ao100 = avg(entries, 100);
        Statistics.Avg session = avg(entries, entries.size());
        return new Statistics(ao5, ao12, ao50, ao100, session);
    }

    public void clear(int width, int height, int mode, boolean hard) {
        Key key = new Key(width, height, mode, hard);
        records.remove(key);
    }

    public void clear() {
        records.clear();
    }

    private static Statistics.Avg avg(List<Entry> entries, int num) {
        int size = entries.size();
        if (size < num) {
            return null;
        }
        List<Entry> lastN;
        if (size == num) {
            lastN = new ArrayList<>(entries);
        } else {
            // take last 'num' entries
            lastN = new ArrayList<>(entries.subList(size - num, size));
        }
        Collections.sort(lastN);
        if (size > 2) {
            // remove best and worst solves
            lastN.remove(lastN.size() - 1);
            lastN.remove(0);
        }
        return avg(lastN);
    }

    private static Statistics.Avg avg(List<Entry> entries) {
        if (entries.isEmpty()) {
            return null;
        }
        int size = entries.size();
        long totalTime = 0;
        int totalMoves = 0;
        for (Entry entry : entries) {
            totalTime += entry.time;
            totalMoves += entry.moves;
        }
        long time = totalTime / size;
        float moves = (float) totalMoves / size;
        float tps = (float) totalMoves / (float) totalTime * 1000f;
        return new Statistics.Avg(time, moves, tps);
    }

    private static class Key {

        final int width;
        final int height;
        final int mode;
        final boolean hard;

        private Key(int width, int height, int mode, boolean hard) {
            this.width = width;
            this.height = height;
            this.mode = mode;
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
            if (mode != key.mode) {
                return false;
            }
            return hard == key.hard;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + mode;
            result = 31 * result + (hard ? 1 : 0);
            return result;
        }
    }

    private static class Entry implements Comparable<Entry> {

        final long time;
        final int moves;

        private Entry(long time, int moves) {
            this.time = time;
            this.moves = moves;
        }

        @Override
        public int compareTo(Entry other) {
            return Long.compare(this.time, other.time);
        }
    }
}
