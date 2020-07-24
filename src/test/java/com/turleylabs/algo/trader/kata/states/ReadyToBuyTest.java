package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Bar;
import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;
import com.turleylabs.algo.trader.kata.framework.Slice;
import org.junit.Before;
import org.junit.Test;


import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class ReadyToBuyTest {

    public static final String TQQQ = "TQQQ";
    private static final double ARBITRARY_PRICE = 98.6;
    private final RefactorMeAlgorithm algorithm = new RefactorMeAlgorithm();
    private final StringBuilder holdingValue = new StringBuilder();
    private final Consumer<String> holdingsFunction = holdingValue::append;
    private final StringBuilder loggedValue = new StringBuilder();
    private final Consumer<String> logFunction = loggedValue::append;
    private static final Averages AVERAGES = createAverages(100.0, 99.0, 102.0, 97.0);
    static final double ARBITRARY_LAST_VIX_CLOSE = 18.0;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CANADA);
    }

    @Test
    public void basicBuy_TransitionToBoughtBelow50_When_ShouldBuyAndPriceBelowMA50() {
        var priceBelow50MA = AVERAGES.movingAverage50.getValue() - 1;
        var state = createReadyToBuy(shouldBuy(true));

        var result = state.onData(getSlice(priceBelow50MA), TQQQ, AVERAGES, ARBITRARY_LAST_VIX_CLOSE);

        assertSame(algorithm.BOUGHT_BELOW_50, result);
        assertBought(TQQQ, "0.0100"); // TODO from whence comes the % above MA?
    }

    @Test
    public void basicBuy_TransitionToBoughtAbove50_When_ShouldBuyAndPriceAboveMA50() {
        var priceAbove50MA = AVERAGES.movingAverage50.getValue() + 1;
        var state = createReadyToBuy(shouldBuy(true));

        var result = state.onData(getSlice(priceAbove50MA), TQQQ, AVERAGES, ARBITRARY_LAST_VIX_CLOSE);

        assertSame(algorithm.BOUGHT_ABOVE_50, result);
        assertBought(TQQQ, "0.0300");
    }

    @Test
    public void basicBuy_NoTransition_When_ShouldNotBuy() {
        var state = createReadyToBuy(shouldBuy(false));

        var result = state.onData(getSlice(ARBITRARY_PRICE), TQQQ, AVERAGES, ARBITRARY_LAST_VIX_CLOSE);

        assertSame(algorithm.READY_TO_BUY, result);
        assertNotBought();
    }

    private void assertNotBought() {
        assertEquals("", holdingValue.toString());
    }

    private void assertBought(String symbol, String percentAboveMA) {
        assertEquals(format("Buy %s Vix %s. above 10 MA %s", symbol, format(ReadyToBuy.PRICE_FORMAT, ARBITRARY_LAST_VIX_CLOSE), percentAboveMA), loggedValue.toString());
        assertEquals(symbol, holdingValue.toString());
    }

    private ReadyToBuy createReadyToBuy(ShouldBuy shouldBuy) {
        return new ReadyToBuy(algorithm, holdingsFunction, logFunction, null, shouldBuy);
    }

    private ShouldBuy shouldBuy(final boolean shouldBuy) {
        return new ShouldBuy() {
                @Override
                boolean shouldBuy(Slice data, String symbol, Averages averages, double lastVixClose) {
                    return shouldBuy;
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
        SimpleMovingAverage sma10 = new StubMovingAverage(TQQQ, 10, movingAverageWindowSize10);
        SimpleMovingAverage sma21 = new StubMovingAverage(TQQQ, 21, movingAverageWindowSize21);
        SimpleMovingAverage sma50 = new StubMovingAverage(TQQQ, 50, movingAverageWindowSize50);
        SimpleMovingAverage sma200 = new StubMovingAverage(TQQQ, 200, movingAverageWindowSize200);
        return new Averages(sma200, sma50, sma21, sma10);
    }
}
