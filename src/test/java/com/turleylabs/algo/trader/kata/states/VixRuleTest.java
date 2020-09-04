package com.turleylabs.algo.trader.kata.states;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VixRuleTest {
    @Test
    public void failsWhenInsufficientDays() {
        double lastVixClose = 0.0;
        double entryThreshold = 0.0;
        var days = Arrays.asList(1.0);

        boolean result = VixRule.apply(lastVixClose, entryThreshold, days);

        Assert.assertFalse(result);
    }

    @Test
    public void passesWhenSomething() {
        double lastVixClose = 0.0;
        double entryThreshold = 100.0;
        var days = Arrays.asList(1.0);

        boolean result = VixRule.apply(lastVixClose, entryThreshold, days);

        Assert.assertTrue(result);
    }
}