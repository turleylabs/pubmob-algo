package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Slice;

public class ShouldBuy {
    boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {
        return averages.arePricesRisingNearTerm(data, symbol)
                && !(averages.isPriceCloseToPeak(data, symbol))
                && lastVixClose < RefactorMeAlgorithm.ENTRY_THRESHOLD
                && averages.isPriceNearShortTermAverage(data, symbol);
    }
}
