package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.CBOE;
import com.turleylabs.algo.trader.kata.framework.Holding;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.Map;

public interface RefactorMeLogger {
    void logSellAction(Slice data, Averages averages, String symbol, CBOE lastVix, Map<String, Holding> portfolio);

    void log2(String logline);
}
