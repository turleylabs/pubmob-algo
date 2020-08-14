package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public class ShouldBuy {
    private ArrayList<Double> day = new ArrayList<Double>(0);
    private Double halfHigh = 0.0;
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high

    boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {

        day.add(0, lastVixClose);

        // two days memory
        if (vixRulesStateBuy()) {
            return true;
        }

        return averages.arePricesRisingNearTerm(data, symbol)
                && !(averages.isPriceCloseToPeak(data, symbol))
                && lastVixClose < 1 // RefactorMeAlgorithm.ENTRY_THRESHOLD
                && averages.isPriceNearShortTermAverage(data, symbol);
    }

    private boolean vixRulesStateBuy() {
        return twoDaysInARowBelowHalfOfTheHigh();
    }

    private boolean twoDaysInARowBelowHalfOfTheHigh() {
        if ( day.size() < 2) {
            return false;
        }
        Stream<Double> stream = day.stream();
        OptionalDouble optionalDouble = stream.mapToDouble(n -> n.doubleValue()).max();
        this.halfHigh = optionalDouble.getAsDouble() / 2;
        return  day.get(0) < this.halfHigh && day.get(1) < this.halfHigh;
    }

}
