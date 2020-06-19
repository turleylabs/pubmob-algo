package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.RefactorMeLogger;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.util.function.Consumer;

public class ReadyToBuy extends ProfitState {

    private final RefactorMeAlgorithm refactorMeAlgorithm;
    private final Consumer<String> setHoldingsFunction;
    private final Consumer<String> logFunction;
    RefactorMeLogger logger;

    public ReadyToBuy(RefactorMeAlgorithm refactorMeAlgorithm, Consumer<String> holdingsFunction, Consumer<String> logFunction, RefactorMeLogger logger) {
        this.refactorMeAlgorithm = refactorMeAlgorithm;
        setHoldingsFunction = holdingsFunction;
        this.logFunction = logFunction;
        this.logger = logger;
    }

    @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            if (averages.arePricesRisingNearTerm(data, symbol)
                    && !(averages.isPriceCloseToPeak(data, symbol))
                    && refactorMeAlgorithm.hasLowVolatility(refactorMeAlgorithm.lastVix)
                    && averages.isPriceNearShortTermAverage(data, symbol)) {
                String logLine = String.format("Buy %s Vix %.4f. above 10 MA %.4f", symbol, refactorMeAlgorithm.lastVix.getClose(), (data.get(symbol).getPrice() - averages.movingAverage10.getValue()) / averages.movingAverage10.getValue()
                );
                logFunction.accept(logLine);
                setHoldingsFunction.accept(symbol);

                return data.get(symbol).getPrice() < averages.movingAverage50.getValue() ? refactorMeAlgorithm.WE_HOLD_POSITIONS : refactorMeAlgorithm.BOUGHT_ABOVE_50;
            }
            return refactorMeAlgorithm.READY_TO_BUY;
        }
}
