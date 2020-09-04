package com.turleylabs.algo.trader.kata.states;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public class VixRule {
    public static boolean twoDaysInARowBelowHalfOfTheHigh(List<Double> day) {
        if ( day.size() < 2) {
            return false;
        }
        Stream<Double> stream = day.stream();
        OptionalDouble optionalDouble = stream.mapToDouble(n -> n.doubleValue()).max();
        Double halfHigh = optionalDouble.getAsDouble() / 2;
        Double oneDayAgo = day.get(0);
        Double twoDaysAgo = day.get(1);
        return  oneDayAgo < halfHigh && twoDaysAgo < halfHigh;
    }

    public static boolean apply(double lastVixClose, double entryThreshold, List<Double> day) {
        return lastVixClose < entryThreshold || VixRule.twoDaysInARowBelowHalfOfTheHigh(day);
    }
}
