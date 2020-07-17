package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.framework.Slice;

public abstract class ProfitState {
    public abstract ProfitState onData(Slice data, String symbol, Averages averages, double lastVixClose);
}
