package com.italankin.fifteen.statistics;

import java.util.Comparator;

public class StatisticsEntry {

    static final Comparator<StatisticsEntry> BY_TIME = (lhs, rhs) -> {
        return Long.compare(lhs.time, rhs.time);
    };
    static final Comparator<StatisticsEntry> BY_MOVES = (lhs, rhs) -> {
        return Long.compare(lhs.moves, rhs.moves);
    };
    static final Comparator<StatisticsEntry> BY_TPS = (lhs, rhs) -> {
        return Float.compare(lhs.tps, rhs.tps);
    };

    public final long time;
    public final int moves;
    public final float tps;

    StatisticsEntry(long time, int moves) {
        this.time = time;
        this.moves = moves;
        this.tps = (float) moves / (float) time * 1000f;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "time=" + time +
                ", moves=" + moves +
                ", tps=" + tps +
                '}';
    }
}
