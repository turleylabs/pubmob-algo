package com.turleylabs.algo.trader.kata.states;

public class VixRule {
    public static boolean apply(double entryThreshold, VixCloseHistory vixCloseHistory) {
        return (vixCloseHistory.lastVixClose() < entryThreshold) || vixCloseHistory.twoDaysInARowBelowHalfOfTheHigh();
    }

}
