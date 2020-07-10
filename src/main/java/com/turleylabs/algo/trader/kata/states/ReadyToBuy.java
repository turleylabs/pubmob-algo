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

    public ReadyToBuy(RefactorMeAlgorithm algorithm, Consumer<String> holdingsFunction, Consumer<String> logFunction, RefactorMeLogger logger) {
        this.algorithm = algorithm;
        setHoldingsFunction = holdingsFunction;
        this.logFunction = logFunction;
        this.logger = logger;
    }

    @Override
    public ProfitState onData(Slice data, String symbol, Averages averages) {
        if (shouldBuy(data, symbol, averages)) {
            String logLine = String.format("Buy %s Vix " + PRICE_FORMAT + ". above 10 MA %.4f",
                    symbol,
                    algorithm.lastVix.getClose(),
                    (data.get(symbol).getPrice() - averages.movingAverage10.getValue()) / averages.movingAverage10.getValue());
            logFunction.accept(logLine);
            setHoldingsFunction.accept(symbol);

            return data.get(symbol).getPrice() < averages.movingAverage50.getValue()
                    ? algorithm.WE_HOLD_POSITIONS
                    : algorithm.BOUGHT_ABOVE_50;
        }
        return algorithm.READY_TO_BUY;
    }

    private boolean shouldBuy(Slice data, String symbol, Averages averages) {
        return averages.arePricesRisingNearTerm(data, symbol)
                && !(averages.isPriceCloseToPeak(data, symbol))
                && algorithm.lastVix.getClose() < RefactorMeAlgorithm.ENTRY_THRESHOLD
                && averages.isPriceNearShortTermAverage(data, symbol);
    }

    @Override
    public String toString() {
        return "ReadyToBuy{}";
    }
}
