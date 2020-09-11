package com.turleylabs.algo.trader.kata.states;

public class VixRule {
    public boolean apply(double entryThreshold, VixCloseHistory vixCloseHistory) {
        return vixCloseHistory.enoughHistory()
               && (vixCloseHistory.isLastVixCloseLowerThan(entryThreshold)
                    || vixCloseHistory.twoDaysInARowBelowHalfOfTheHigh());
    }



}
