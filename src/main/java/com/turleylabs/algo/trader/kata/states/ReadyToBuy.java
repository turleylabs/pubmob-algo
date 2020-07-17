package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.RefactorMeLogger;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.function.Consumer;

public class ReadyToBuy extends ProfitState {

    public static final String PRICE_FORMAT = "%.4f";
    private final RefactorMeAlgorithm algorithm;
    private final Consumer<String> setHoldingsFunction;
    private final Consumer<String> logFunction;
    RefactorMeLogger logger;
    private final ShouldBuy shouldBuy;

    public ReadyToBuy(RefactorMeAlgorithm algorithm, Consumer<String> holdingsFunction, Consumer<String> logFunction, RefactorMeLogger logger, ShouldBuy shouldBuy) {
        this.algorithm = algorithm;
        setHoldingsFunction = holdingsFunction;
        this.logFunction = logFunction;
        this.logger = logger;
        this.shouldBuy = shouldBuy;
    }

    @Override
    public ProfitState onData(Slice data, String symbol, Averages averages, double lastVixClose) {
        if (shouldBuy.shouldBuy(data, symbol, averages, lastVixClose)) {
            String logLine = String.format("Buy %s Vix " + PRICE_FORMAT + ". above 10 MA %.4f",
                    symbol,
                    lastVixClose,
                    (data.get(symbol).getPrice() - averages.movingAverage10.getValue()) / averages.movingAverage10.getValue());
            logFunction.accept(logLine);
            setHoldingsFunction.accept(symbol);

            return data.get(symbol).getPrice() < averages.movingAverage50.getValue()
                    ? algorithm.WE_HOLD_POSITIONS
                    : algorithm.BOUGHT_ABOVE_50;
        }
        return algorithm.READY_TO_BUY;
    }

    @Override
    public String toString() {
        return "ReadyToBuy{}";
    }

}
