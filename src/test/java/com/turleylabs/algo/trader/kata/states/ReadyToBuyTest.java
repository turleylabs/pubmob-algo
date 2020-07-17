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
import static org.junit.Assert.*;

public class ReadyToBuyTest {

    private final RefactorMeAlgorithm algorithm = new RefactorMeAlgorithm();
    private final StringBuilder holdingValue = new StringBuilder();
    private final Consumer<String> holdingsFunction = holdingValue::append;
    private final StringBuilder loggedValue = new StringBuilder();
    private final Consumer<String> logFunction = loggedValue::append;
    private static final Averages TRANSITION_TO_WE_HOLD_POSITIONS = createAverages(100.0, 99.0, 102.0, 97.0);

    @Before
    public void setup() {
        Locale.setDefault(Locale.CANADA);
    }

    @Test
    public void basicBuy_TransitionToWeHoldPositions_When_ShouldBuyAndPriceBelowMA50() {
        var priceBelow50MA = 101.0;
        double lastVixClose = 18.0;

        algorithm.lastVix = new CBOE(lastVixClose);
        var state = new ReadyToBuy(algorithm, holdingsFunction, logFunction, null, doBuy());
        var slice = getSlice(priceBelow50MA);

        var result = state.onData(slice, "TQQQ", TRANSITION_TO_WE_HOLD_POSITIONS, lastVixClose);

        assertSame(algorithm.WE_HOLD_POSITIONS, result);
        assertEquals(format("Buy TQQQ Vix %s. above 10 MA 0.0100", format(ReadyToBuy.PRICE_FORMAT, lastVixClose)), loggedValue.toString());
        assertEquals("TQQQ", holdingValue.toString());
    }

    private ShouldBuy doBuy() {
        return new ShouldBuy() {
                @Override
                boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {
                    return true;
                }
            };
    }

    private Slice getSlice(double priceBelow50MA) {
        LocalDate tradeDate = LocalDate.of(2012, 1, 3);
        return new Slice(tradeDate) {
            @Override
            public Bar get(String symbol) {
                return new Bar(priceBelow50MA);
            }
        };
    }

    private static Averages createAverages(double movingAverageWindowSize10, double movingAverageWindowSize21, double movingAverageWindowSize50, double movingAverageWindowSize200) {
        SimpleMovingAverage sma10 = new StubMovingAverage("TQQQ", 10, movingAverageWindowSize10);
        SimpleMovingAverage sma21 = new StubMovingAverage("TQQQ", 21, movingAverageWindowSize21);
        SimpleMovingAverage sma50 = new StubMovingAverage("TQQQ", 50, movingAverageWindowSize50);
        SimpleMovingAverage sma200 = new StubMovingAverage("TQQQ", 200, movingAverageWindowSize200);
        return new Averages(sma200, sma50, sma21, sma10);
    }
}
