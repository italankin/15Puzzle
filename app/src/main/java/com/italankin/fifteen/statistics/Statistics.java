package com.italankin.fifteen.statistics;

public class Statistics {

    public static final Statistics EMPTY = new Statistics(null, null, null, null, null);

    public final Avg ao5;
    public final Avg ao12;
    public final Avg ao50;
    public final Avg ao100;
    public final Avg session;

    public Statistics(Avg ao5, Avg ao12, Avg ao50, Avg ao100, Avg session) {
        this.ao5 = ao5;
        this.ao12 = ao12;
        this.ao50 = ao50;
        this.ao100 = ao100;
        this.session = session;
    }

    public boolean isEmpty() {
        return session == null;
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
    }
}
