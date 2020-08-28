package com.turleylabs.algo.trader.kata.states;

import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * @author Rahul Agrawal
 *         on 28/08/20
 *         algo-trader-kata
 */
public class VixRule {

    private ArrayList<Double> day;

    public VixRule(ArrayList<Double> day) {
        this.day = day;
    }

    public static boolean twoDaysInARowBelowHalfOfTheHigh(ArrayList<Double> day) {
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

    public boolean apply(double lastVixClose, double entryThreshold) {
        return lastVixClose < entryThreshold || VixRule.twoDaysInARowBelowHalfOfTheHigh(day);
    }
}
