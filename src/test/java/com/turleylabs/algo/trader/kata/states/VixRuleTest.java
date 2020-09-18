package com.turleylabs.algo.trader.kata.states;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class VixRuleTest {

    private final VixCloseHistory vixCloseHistory = new VixCloseHistory();

    @Test
    public void failsWhenInsufficientDays() {
        double entryThreshold = 0.0;
        vixCloseHistory.dailyVixCloses = Arrays.asList(0.0);

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        Assert.assertFalse(result);
    }

    @Test
    public void failsWhenInsufficientDays_2() {
        double entryThreshold = 0.0;
        vixCloseHistory.dailyVixCloses = Arrays.asList();

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        Assert.assertFalse(result);
    }


    @Test
    public void passesWhenLastVixCloseIsLessThanEntryThreshold() {
        double entryThreshold = 100.0;
        vixCloseHistory.dailyVixCloses = Arrays.asList(entryThreshold - 1.0, 50.0);

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        assertTrue(result);
    }

    @Test
    public void passesWhenTwoDaysInARowAreBelowHalfOfTheHigh() {
        double entryThreshold = 19.0;
        var daysMustAscend = Arrays.asList(entryThreshold + 1.0, entryThreshold + 2.0, entryThreshold * 3);
        vixCloseHistory.dailyVixCloses = daysMustAscend;

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        assertTrue(result);
    }

    @Test
    public void failsWhenOneDayAgoAboveHalfHigh() {
        double entryThreshold = 19.0;
        double high = entryThreshold * 3.0;
        vixCloseHistory.dailyVixCloses = Arrays.asList(high / 2 + 1.0, entryThreshold + 2.0, high);

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        Assert.assertFalse(result);
    }

    @Test
    public void failsWhenTwoDayAgoAboveHalfHigh() {
        double entryThreshold = 19.0;
        double high = entryThreshold * 3.0;
        vixCloseHistory.dailyVixCloses = Arrays.asList(high / 2 + 1.0, high / 2 + 1.0, high);

        boolean result = new VixRule().apply(entryThreshold, vixCloseHistory);

        Assert.assertFalse(result);
    }
}
