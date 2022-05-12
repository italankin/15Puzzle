package com.italankin.fifteen.statistics;

public class Statistics {

    public static final Statistics EMPTY = new Statistics(
            0,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    public final int totalCount;
    public final Avg single;
    public final Avg ao5;
    public final Avg ao12;
    public final Avg ao50;
    public final Avg ao100;
    public final Avg session;
    public final Avg bestTime;
    public final Avg bestMoves;
    public final Avg worstTime;
    public final Avg worstMoves;

    public Statistics(
            int totalCount,
            Avg single,
            Avg ao5,
            Avg ao12,
            Avg ao50,
            Avg ao100,
            Avg session,
            Avg bestTime,
            Avg bestMoves,
            Avg worstTime,
            Avg worstMoves) {
        this.totalCount = totalCount;
        this.single = single;
        this.ao5 = ao5;
        this.ao12 = ao12;
        this.ao50 = ao50;
        this.ao100 = ao100;
        this.session = session;
        this.bestTime = bestTime;
        this.bestMoves = bestMoves;
        this.worstTime = worstTime;
        this.worstMoves = worstMoves;
    }

    public boolean isEmpty() {
        return session == null;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "ao5=" + ao5 +
                ", ao12=" + ao12 +
                ", ao50=" + ao50 +
                ", ao100=" + ao100 +
                ", bestTime=" + bestTime +
                ", bestMoves=" + bestMoves +
                ", worstTime=" + worstTime +
                ", worstMoves=" + worstMoves +
                '}';
    }

    public static class Avg {

        public final long time;
        public final float moves;
        public final float tps;

        Avg(long time, float moves, float tps) {
            this.time = time;
            this.moves = moves;
            this.tps = tps;
        }

        @Override
        public String toString() {
            return '(' +
                    "time=" + time +
                    ", moves=" + moves +
                    ", tps=" + tps +
                    ')';
        }
    }
}
