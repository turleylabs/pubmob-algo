package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.framework.Slice;
import org.junit.Test;

public class ShouldBuyTest {
    // new functionality test stub.
    // Goal:  initiate a buy when the VIX is greater than the threshold currently in the algorithm,
    // as long as it (the VIX) closes two days in a row below half of it's most recent high
    @Test
    public void asdf(){
        var shouldBuy = new ShouldBuy();
        var data = ReadyToBuyTest.getSlice(100);
        String symbol = "TQQQ";
        Averages averages = ReadyToBuyTest.createAverages(0,0,0,0);


        // call should by day 1 : something high 100
        // call should by day 2 : something else 42
        // call should by day 3 : something else 40
        // call should by day 4 : something else 40
        double lastVixClose1 = 100;
        double lastVixClose2 = 42;
        double lastVixClose3 = 40;
        double lastVixClose4 = 40;
        boolean result = shouldBuy.shouldBuy(data, symbol, averages, lastVixClose1);



        // verify day 4 it wants buy (i.e. shouldBuy is true)
    }
}
