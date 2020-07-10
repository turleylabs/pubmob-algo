package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;

public class StubMovingAverage extends SimpleMovingAverage {

    private final double value;

    public StubMovingAverage(String symbol, int windowSize, double value) {
        super(symbol, windowSize);
        this.value = value;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
