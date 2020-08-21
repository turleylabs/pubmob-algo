package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ShouldBuyTest {
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high
    @Test
    public void shouldBuyHiLoLoLo(){

        // given that the reason that this does not work is only the VIX buy rule,
        // then this SHOULD work
        
        double day1 = 100;
        double day2 = 42;
        double day3 = 40;
        double day4 = 40;
        boolean finalDayBought = true;

        verifyShouldBuy(finalDayBought, day1, day2, day3, day4);
    }

    @Test
    public void testShouldNotBuySolelyOnlyBecauseOfVixRule() {
        Averages averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);
        String symbol = "TQQQ";

        boolean result = new ShouldBuy().shouldBuy(data, symbol, averages, 40);
        Assert.assertEquals(false, result);

        var shouldBuy = new ShouldBuy(){
            @Override
            public boolean vixRule(double lastVixClose, double entryThreshold) {
                return true;
            }
        };
        result = shouldBuy.shouldBuy(data, symbol, averages, 40);
        Assert.assertEquals(true, result);
    }


    @Ignore
    @Test
    public void loHiLoLoHiHoHiHo(){
        verifyShouldBuy(false, (double) 50, (double) 42, (double) 40, (double) 40);
    }

    private void verifyShouldBuy(boolean finalDayBought, double... days) {
        Averages averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var shouldBuy = new ShouldBuy();
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);
        String symbol = "TQQQ";

        Assert.assertFalse(shouldBuy.shouldBuy(data, symbol, averages, days[0]));
        Assert.assertFalse(shouldBuy.shouldBuy(data, symbol, averages, days[1]));
        Assert.assertFalse(shouldBuy.shouldBuy(data, symbol, averages, days[2]));
        boolean result = shouldBuy.shouldBuy(data, symbol, averages, days[3]);
        Assert.assertEquals(finalDayBought, result);
    }
}

