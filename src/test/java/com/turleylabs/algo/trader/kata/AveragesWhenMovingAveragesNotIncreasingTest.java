package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.states.ReadyToBuyTest;
import com.turleylabs.algo.trader.kata.states.StubMovingAverage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AveragesWhenMovingAveragesNotIncreasingTest {

    public static final String SYMBOL = "TQQQ";
    private static final double TOLERANCE = 0.05;
    public static final int MA_10_VALUE = 10;
    private Averages averages;
    private StubMovingAverage movingAverage21;
    private StubMovingAverage movingAverage10;

    @Before
    public void setup(){
        createIncreasingAverages(MA_10_VALUE - 1);
    }

    @Test
    public void pricesAreNotRisingWhen10DayAverageLessThan21DayAverage() {

        movingAverage21.setValue(MA_10_VALUE + 1);
        var symbolPrice = MA_10_VALUE + 1;
        var data = ReadyToBuyTest.getSlice(symbolPrice);

        assertFalse(averages.arePricesRisingNearTerm(data, SYMBOL));
    }

    @Test
    public void pricesAreNotRisingWhenMA10isNotMoreThanPMA10() {
        movingAverage10.setValue(0);
        var symbolPrice = MA_10_VALUE + 1;
        var data = ReadyToBuyTest.getSlice(symbolPrice);

        assertFalse(averages.arePricesRisingNearTerm(data, SYMBOL));
    }

    @Test
    public void pricesAreNotRisingWhenMA21isNotMoreThanPMA21() {
        movingAverage21.setValue(0);
        var symbolPrice = MA_10_VALUE + 1;
        var data = ReadyToBuyTest.getSlice(symbolPrice);

        assertFalse(averages.arePricesRisingNearTerm(data, SYMBOL));
    }



    private void createIncreasingAverages(int ma21Value) {
        movingAverage21 = new StubMovingAverage(SYMBOL, 21, ma21Value);
        movingAverage10 = new StubMovingAverage(SYMBOL, 10, MA_10_VALUE);
        averages = new Averages(
                new StubMovingAverage(SYMBOL, 200, 0),
                new StubMovingAverage(SYMBOL, 50, 0),
                movingAverage21,
                movingAverage10
        );
        setPreviousAveragesToZero();
    }

    private void setPreviousAveragesToZero() {
        averages.previousMovingAverage10 = 0;
        averages.previousMovingAverage21 = 0;
        averages.previousMovingAverage50 = 0;
    }
}