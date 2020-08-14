package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import org.junit.Assert;
import org.junit.Test;

public class ShouldBuyTest {
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high
    @Test
    public void shouldBuyHiLoLoLo(){
        double day1 = 100;
        double day2 = 42;
        double day3 = 40;
        double day4 = 40;
        boolean finalDayBought = true;

        verifyShouldBuy(finalDayBought, day1, day2, day3, day4);
    }

    @Test
    public void loHiLoLo(){
        verifyShouldBuy(false, (double) 50, (double) 42, (double) 40, (double) 40);
    }

    private void verifyShouldBuy(boolean finalDayBought, double... days) {
        var shouldBuy = new ShouldBuy();
        var data = ReadyToBuyTest.getSlice(100);
        String symbol = "TQQQ";
        Averages averages = ReadyToBuyTest.createAverages(0, 0, 0, 0);

        shouldBuy.shouldBuy(data, symbol, averages, days[0]);
        shouldBuy.shouldBuy(data, symbol, averages, days[1]);
        shouldBuy.shouldBuy(data, symbol, averages, days[2]);
        boolean result = shouldBuy.shouldBuy(data, symbol, averages, days[3]);
        Assert.assertEquals(finalDayBought, result);
    }


}

