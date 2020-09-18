package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.states.ReadyToBuyTest;
import com.turleylabs.algo.trader.kata.states.StubMovingAverage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AveragesWhenMovingAveragesIncreasingTest {

    public static final String SYMBOL = "TQQQ";
    private static final double TOLERANCE = 0.05;
    public static final int MA_10_VALUE = 10;
    private Averages averages;

    @Before
    public void setup(){
        var ma21Value = MA_10_VALUE - 1;
        createIncreasingAverages(ma21Value);
    }

    @Test
    public void pricesAreRisingWhenPriceMoreThan10DayAverage() {

        var symbolPrice = MA_10_VALUE + 1;
        var data = ReadyToBuyTest.getSlice(symbolPrice);

        assertTrue(averages.arePricesRisingNearTerm(data, SYMBOL));
    }

    @Test
    public void pricesAreNotRisingWhenPriceLessThan10DayAverage() {

        var symbolPrice = MA_10_VALUE - 1;
        var data = ReadyToBuyTest.getSlice(symbolPrice);

        assertFalse(averages.arePricesRisingNearTerm(data, SYMBOL));
    }

    private void createIncreasingAverages(int ma21Value) {
         averages = new Averages(
                new StubMovingAverage(SYMBOL, 200, 0),
                new StubMovingAverage(SYMBOL, 50, 0),
                new StubMovingAverage(SYMBOL, 21, ma21Value),
                new StubMovingAverage(SYMBOL, 10, MA_10_VALUE));
        setPreviousAveragesToZero();
    }

    private void setPreviousAveragesToZero() {
        averages.previousMovingAverage10 = 0;
        averages.previousMovingAverage21 = 0;
        averages.previousMovingAverage50 = 0;
    }
}