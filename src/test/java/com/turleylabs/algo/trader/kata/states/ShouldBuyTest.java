package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import org.junit.Assert;
import org.junit.Test;

public class ShouldBuyTest {
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high
    @Test
    public void somethingShouldBuy(){
        var shouldBuy = new ShouldBuy();
        var data = ReadyToBuyTest.getSlice(100);
        String symbol = "TQQQ";
        Averages averages = ReadyToBuyTest.createAverages(0,0,0,0);


        shouldBuy.shouldBuy(data, symbol, averages, (double) 100);
        shouldBuy.shouldBuy(data, symbol, averages, (double) 42);
        shouldBuy.shouldBuy(data, symbol, averages, (double) 40);
        boolean result = shouldBuy.shouldBuy(data, symbol, averages, (double) 40);
        Assert.assertTrue(result);

    }
}

