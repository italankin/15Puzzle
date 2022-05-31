package com.italankin.fifteen.statistics;

import com.italankin.fifteen.Constants;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

@RunWith(JUnit4.class)
public class StatisticsManagerTest extends TestCase {

    @Test
    public void single() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 21000, 100);

        Statistics statistics = getEntry(statisticsManager);
        Statistics.Avg expected = new Statistics.Avg(21000, 100, 4.762f);
        assertAvgEquals(expected, statistics.single);
    }

    @Test
    public void singleRolling() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 21000, 100);
        addEntry(statisticsManager, 19850, 99);

        Statistics statistics = getEntry(statisticsManager);
        Statistics.Avg expected = new Statistics.Avg(19850, 99, 4.987f);
        assertAvgEquals(expected, statistics.single);
    }

    @Test
    public void ao5() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 23454, 117);
        addEntry(statisticsManager, 21520, 103);
        addEntry(statisticsManager, 19854, 89);
        addEntry(statisticsManager, 20011, 98);

        Statistics statistics = getEntry(statisticsManager);
        assertEquals(5, statistics.totalCount);
        Statistics.Avg expected = new Statistics.Avg(21661, 102, 4.722f);
        assertAvgEquals(expected, statistics.ao5);
    }

    @Test
    public void ao5Rolling() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 23454, 117);
        addEntry(statisticsManager, 21520, 103);
        addEntry(statisticsManager, 19854, 89);
        addEntry(statisticsManager, 20011, 98);

        Statistics statistics = getEntry(statisticsManager);
        assertEquals(5, statistics.totalCount);
        Statistics.Avg expected = new Statistics.Avg(21661, 102, 4.722f);
        assertAvgEquals(expected, statistics.ao5);

        addEntry(statisticsManager, 21777, 102);

        statistics = getEntry(statisticsManager);
        assertEquals(6, statistics.totalCount);
        expected = new Statistics.Avg(21102, 101, 4.789f);
        assertAvgEquals(expected, statistics.ao5);
    }

    @Test
    public void best() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 21111, 117);

        Statistics statistics = getEntry(statisticsManager);

        Statistics.Avg expectedBestTime = new Statistics.Avg(21111, 117, 5.542f);
        assertAvgEquals(expectedBestTime, statistics.bestTime);

        Statistics.Avg expectedBestMoves = new Statistics.Avg(25011, 105, 4.198f);
        assertAvgEquals(expectedBestMoves, statistics.bestMoves);
    }

    @Test
    public void worst() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 21111, 117);

        Statistics statistics = getEntry(statisticsManager);

        Statistics.Avg expectedWorstTime = new Statistics.Avg(25011, 105, 4.198f);
        assertAvgEquals(expectedWorstTime, statistics.worstTime);

        Statistics.Avg expectedWorstMoves = new Statistics.Avg(21111, 117, 5.542f);
        assertAvgEquals(expectedWorstMoves, statistics.worstMoves);
    }

    @Test
    public void session() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 23454, 117);

        Statistics statistics = getEntry(statisticsManager);
        assertEquals(2, statistics.totalCount);
        assertNull(statistics.session);

        addEntry(statisticsManager, 21520, 103);

        statistics = getEntry(statisticsManager);
        assertEquals(3, statistics.totalCount);
        Statistics.Avg expected = new Statistics.Avg(23454, 105, 4.786f);
        assertAvgEquals(expected, statistics.session);
    }

    @Test
    public void sessionRolling() {
        StatisticsManager statisticsManager = new StatisticsManager(noOpSessionStorage());
        addEntry(statisticsManager, 25011, 105);
        addEntry(statisticsManager, 23454, 117);
        addEntry(statisticsManager, 21520, 103);

        Statistics statistics = getEntry(statisticsManager);
        assertEquals(3, statistics.totalCount);
        Statistics.Avg expected = new Statistics.Avg(23454, 105, 4.786f);
        assertAvgEquals(expected, statistics.session);

        addEntry(statisticsManager, 22000, 110);

        statistics = getEntry(statisticsManager);
        assertEquals(4, statistics.totalCount);
        expected = new Statistics.Avg(22727, 107.5f, 4.887f);
        assertAvgEquals(expected, statistics.session);

        addEntry(statisticsManager, 24121, 109);

        statistics = getEntry(statisticsManager);
        assertEquals(5, statistics.totalCount);
        expected = new Statistics.Avg(23191, 108, 4.765f);
        assertAvgEquals(expected, statistics.session);

        addEntry(statisticsManager, 24500, 111);

        statistics = getEntry(statisticsManager);
        assertEquals(6, statistics.totalCount);
        expected = new Statistics.Avg(23518, 108.75f, 4.706f);
        assertAvgEquals(expected, statistics.session);
    }

    private static void assertAvgEquals(Statistics.Avg expected, Statistics.Avg actual) {
        assertEquals(expected.time, actual.time);
        assertEquals(expected.moves, actual.moves, 0.001);
        assertEquals(expected.tps, actual.tps, 0.001);
    }

    private static Statistics getEntry(StatisticsManager statisticsManager) {
        return statisticsManager.get(4, 4, Constants.TYPE_CLASSIC, false);
    }

    private static void addEntry(StatisticsManager statisticsManager, long time, int moves) {
        statisticsManager.add(4, 4, Constants.TYPE_CLASSIC, false, time, moves);
    }

    private static NoOpSessionStorage noOpSessionStorage() {
        try {
            return new NoOpSessionStorage(File.createTempFile("sessionFile", "tmp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
