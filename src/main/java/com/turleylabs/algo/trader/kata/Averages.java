package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;
import com.turleylabs.algo.trader.kata.framework.Slice;

public class Averages {
    public SimpleMovingAverage movingAverage200;
    public SimpleMovingAverage movingAverage50;
    public SimpleMovingAverage movingAverage21;
    public SimpleMovingAverage movingAverage10;
    public double previousMovingAverage50;
    public double previousMovingAverage21;
    public double previousMovingAverage10;

    public Averages(SimpleMovingAverage movingAverage200, SimpleMovingAverage movingAverage50, SimpleMovingAverage movingAverage21, SimpleMovingAverage movingAverage10) {
        this.movingAverage200 = movingAverage200;
        this.movingAverage50 = movingAverage50;
        this.movingAverage21 = movingAverage21;
        this.movingAverage10 = movingAverage10;
    }

    public void updatePreviousAverages() {
        previousMovingAverage50 = movingAverage50.getValue();
        previousMovingAverage21 = movingAverage21.getValue();
        previousMovingAverage10 = movingAverage10.getValue();
    }

    public boolean arePricesRisingNearTerm(Slice data, String symbol) {
        return data.get(symbol).getPrice() > movingAverage10.getValue()
                && movingAverage10.getValue() > movingAverage21.getValue()
                && movingAverage10.getValue() > previousMovingAverage10
                && movingAverage21.getValue() > previousMovingAverage21;
    }

    public boolean did10DayMACrossBelow21DayMA() {
        return movingAverage10.getValue() < 0.97 * movingAverage21.getValue();
    }

    public boolean isPriceCloseToPeak(Slice data, String symbol) {
        return data.get(symbol).getPrice() >= (movingAverage50.getValue() * 1.15)
                && data.get(symbol).getPrice() >= (movingAverage200.getValue() * 1.40);
    }

    public boolean priceBelow50DayMovingAverage(Slice data, String symbol) {
        return data.get(symbol).getPrice() < (movingAverage50.getValue() * .93);
    }

    public boolean isPriceNearShortTermAverage(Slice data, String symbol) {
        return (data.get(symbol).getPrice() - movingAverage10.getValue()) / movingAverage10.getValue() < 0.07;
    }
}
