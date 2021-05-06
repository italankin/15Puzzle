package com.italankin.fifteen.statistics;

public class StatisticsKey {

    public final int width;
    public final int height;
    public final int type;
    public final boolean hard;

    StatisticsKey(int width, int height, int type, boolean hard) {
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
        StatisticsKey key = (StatisticsKey) o;
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

    @Override
    public String toString() {
        return "Key{" +
                "width=" + width +
                ", height=" + height +
                ", type=" + type +
                ", hard=" + hard +
                '}';
    }
}
