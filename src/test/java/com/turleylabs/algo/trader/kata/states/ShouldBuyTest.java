package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ShouldBuyTest {

    public static final String SYMBOL = "TQQQ";
    public static final VixRule FAILED_VIX_RULE = new VixRule() {
        @Override
        public boolean apply(double entryThreshold, VixCloseHistory vixCloseHistory) {
            return false;
        }
    };
    public static final VixRule PASSED_VIX_RULE = new VixRule() {
        @Override
        public boolean apply(double entryThreshold, VixCloseHistory vixCloseHistory) {
            return true;
        }
    };

    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high
    @Test
    public void shouldBuyHiLoLoLo(){
        var days = new double[]{100, 42, 40, 40};
        var averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);

        var result = new ShouldBuy().shouldBuy(data, SYMBOL, averages, days[3]);

        Assert.assertEquals(true, result);
    }

    @Test
    public void shouldNotBuyLoHiLoLo(){
        var days = new double[]{(double) 50, (double) 42, (double) 40, (double) 40};
        var averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);

        var  result = new ShouldBuy().shouldBuy(data, SYMBOL, averages, days[3]);

        Assert.assertEquals(false, result);
    }

    @Test
    public void shouldNotBuyWhenVixRuleFails() {
        var averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);
        var shouldBuy = new ShouldBuy(FAILED_VIX_RULE);

        var result = shouldBuy.shouldBuy(data, SYMBOL, averages, 40);

        Assert.assertEquals(false, result);
    }

    @Test
    public void shouldBuyWhenVixRulePasses() {
        var averages = ReadyToBuyTest
                .createAverages(100.0, 99.0, 102.0, 97.0);
        var priceBelow50MA = averages.movingAverage50.getValue() - 1;
        var data = ReadyToBuyTest.getSlice(priceBelow50MA);
        var shouldBuy = new ShouldBuy(PASSED_VIX_RULE);

        var result = shouldBuy.shouldBuy(data, SYMBOL, averages, 40);

        Assert.assertEquals(true, result);
    }
}

