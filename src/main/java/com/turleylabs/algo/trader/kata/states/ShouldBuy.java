package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.ArrayList;
import java.util.Arrays;

public class ShouldBuy {
    private ArrayList<Double> day = new ArrayList<Double>(0);
    private Double halfHigh = 500.0;
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high

    boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {

        // two days memory
        if (vixRulesStateBuy()) {
            return true;
        }

        return averages.arePricesRisingNearTerm(data, symbol)
                && !(averages.isPriceCloseToPeak(data, symbol))
                && lastVixClose < RefactorMeAlgorithm.ENTRY_THRESHOLD
                && averages.isPriceNearShortTermAverage(data, symbol);
    }

    private boolean vixRulesStateBuy() {
        return twoDayAverageBelowHalfHigh();
    }

    private boolean twoDayAverageBelowHalfHigh() {
        if ( day.size() < 2) {
            return false;
        }
        halfHigh = day.stream().mapToDouble().max()
        return  day.get(0) < halfHigh  && day.get(1) < halfHigh;
    }

}
