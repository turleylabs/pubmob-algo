package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Bar;
import com.turleylabs.algo.trader.kata.framework.CBOE;
import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;
import com.turleylabs.algo.trader.kata.framework.Slice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;

import static java.lang.String.format;

public class ReadyToBuyTest {

    @Before
    public void setup() {
        Locale.setDefault(Locale.CANADA);
    }

    @Test
    public void basicBuy_TransitionToWeHold_When_ShouldBuyAndPriceBelowMA50() {
        var priceBelow50MA = 101.0;
        double lastVixClose = 18.0;
        var averages = createAverages(100.0, 99.0, 102.0, 97.0);

        RefactorMeAlgorithm algorithm = new RefactorMeAlgorithm();
        algorithm.lastVix = new CBOE(lastVixClose);
        StringBuilder holdingValue = new StringBuilder();
        Consumer<String> holdingsFunction = holdingValue::append;
        StringBuilder loggedValue = new StringBuilder();
        Consumer<String> logFunction = loggedValue::append;
        ReadyToBuy state = new ReadyToBuy(algorithm, holdingsFunction, logFunction, null);
        LocalDate tradeDate = LocalDate.of(2012, 1, 3);
        Slice slice = new Slice(tradeDate) {
            @Override
            public Bar get(String symbol) {
                return new Bar(priceBelow50MA);
            }
        };

        var result = state.onData(slice, "TQQQ", averages);

        Assert.assertSame(algorithm.WE_HOLD_POSITIONS, result);
        Assert.assertEquals(format("Buy TQQQ Vix %s. above 10 MA 0.0100", format(ReadyToBuy.PRICE_FORMAT, lastVixClose)), loggedValue.toString());
        Assert.assertEquals("TQQQ", holdingValue.toString());
    }

    private Averages createAverages(double movingAverageWindowSize10, double movingAverageWindowSize21, double movingAverageWindowSize50, double movingAverageWindowSize200) {
        SimpleMovingAverage sma10 = new StubMovingAverage("TQQQ", 10, movingAverageWindowSize10);
        SimpleMovingAverage sma21 = new StubMovingAverage("TQQQ", 21, movingAverageWindowSize21);
        SimpleMovingAverage sma50 = new StubMovingAverage("TQQQ", 50, movingAverageWindowSize50);
        SimpleMovingAverage sma200 = new StubMovingAverage("TQQQ", 200, movingAverageWindowSize200);
        return new Averages(sma200, sma50, sma21, sma10);
    }
}
