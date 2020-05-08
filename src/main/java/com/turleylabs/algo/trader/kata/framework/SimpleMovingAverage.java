package com.turleylabs.algo.trader.kata.framework;

import java.util.LinkedList;

public class SimpleMovingAverage {
    LinkedList<Double> buffer = new LinkedList<>();
    private String symbol;
    private int windowSize;

    public SimpleMovingAverage(String symbol, int windowSize) {
        this.symbol = symbol;
        this.windowSize = windowSize;
    }

    public double getValue() {
        return this.buffer.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public boolean isReady() {
        return buffer.size() == windowSize;
    }

    public void addData(double data) {
        this.buffer.add(data);

        if (this.buffer.size() > windowSize)
        {
            this.buffer.removeFirst();
        }
    }

    public String getSymbol() {
        return symbol;
    }
}
