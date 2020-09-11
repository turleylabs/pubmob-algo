package com.turleylabs.algo.trader.kata.states;

import java.util.ArrayList;
import java.util.List;

public class VixCloseHistory {
    List<Double> dailyVixCloses = new ArrayList<Double>(0);

    public boolean twoDaysInARowBelowHalfOfTheHigh() {
        if (dailyVixCloses.size() < 2) {
            return false;
        }
        var halfOfMaxClose = max(dailyVixCloses) / 2;
        return dailyVixCloses.stream().limit(2).allMatch(x -> x < halfOfMaxClose);
    }

    private double max(List<Double> numbers) {
        return numbers.stream().mapToDouble(n -> n.doubleValue()).max().getAsDouble();
    }

    public void addClose(double lastVixClose) {
        dailyVixCloses.add(0, lastVixClose);
    }

    public Double lastVixClose() {
        return dailyVixCloses.get(0);
    }

    boolean isLastVixCloseLowerThan(double entryThreshold) {
        return lastVixClose() < entryThreshold;
    }

    public boolean enoughHistory(){
        return dailyVixCloses.size() >= 2;
    }
}