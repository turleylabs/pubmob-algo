package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;
import com.turleylabs.algo.trader.kata.framework.Slice;
import com.turleylabs.algo.trader.kata.states.ReadyToBuyTest;
import com.turleylabs.algo.trader.kata.states.StubMovingAverage;
import org.junit.Test;

import static org.junit.Assert.*;

public class AveragesTest {

    public static final String SYMBOL = "TQQQ";

    @Test
    public void pricesAreRisingNearTermWhenAveragesAreIncreasing() {
        var ma10Value = 10;
        var ma21Value = ma10Value - 1;
        var av = new Averages(
                new StubMovingAverage(SYMBOL,200, 0),
                new StubMovingAverage(SYMBOL,50, 0),
                new StubMovingAverage(SYMBOL, 21, ma21Value),
                new StubMovingAverage(SYMBOL, 10, ma10Value)
        );
        double symbolPrice = ma10Value + 1;
        Slice data = ReadyToBuyTest.getSlice(symbolPrice);

        assertTrue(av.arePricesRisingNearTerm(data, SYMBOL));
    }
}