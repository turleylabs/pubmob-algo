package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Slice;

import static com.turleylabs.algo.trader.kata.RefactorMeAlgorithm.*;

public class ShouldBuy {
    private final VixCloseHistory vixCloseHistory = new VixCloseHistory();
    private VixRule vixRule;

    public ShouldBuy() {
        this(new VixRule());
    }

    public ShouldBuy(VixRule vixRule) {
        this.vixRule = vixRule;
    }
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high

    boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {
        boolean result =
                averages.arePricesRisingNearTerm(data, symbol)
                && !(averages.isPriceCloseToPeak(data, symbol))
                && vixRule.apply(ENTRY_THRESHOLD, vixCloseHistory)
                && averages.isPriceNearShortTermAverage(data, symbol);

        vixCloseHistory.addClose(lastVixClose);

        return result;
    }
}
