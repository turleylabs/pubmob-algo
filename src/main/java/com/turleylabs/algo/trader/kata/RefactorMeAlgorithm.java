package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;

import java.time.LocalDate;
import java.util.Map;

public class RefactorMeAlgorithm extends BaseAlgorithm {

    public static final double USE_ALL_FUNDS = 1.0;
    public final ProfitState READY_TO_BUY = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            if (averages.arePricesRisingNearTerm(data, symbol)
                    && !(averages.isPriceCloseToPeak(data, symbol))
                    && hasLowVolatility(lastVix)
                    && averages.isPriceNearShortTermAverage(data, symbol)) {
                log(String.format("Buy %s Vix %.4f. above 10 MA %.4f", symbol, lastVix.getClose(), (data.get(symbol).getPrice() - averages.movingAverage10.getValue()) / averages.movingAverage10.getValue()
                ));
                setHoldings(symbol, USE_ALL_FUNDS);

                return data.get(symbol).getPrice() < averages.movingAverage50.getValue() ? WE_HOLD_POSITIONS : BOUGHT_ABOVE_50;
            }
            return READY_TO_BUY;
        }
    };
    public final ProfitState WE_HOLD_POSITIONS = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            logSellAction(data, averages, symbol, lastVix, portfolio);

            if ((!hasHighVolatility(lastVix) &&
                    !averages.did10DayMACrossBelow21DayMA())
                    && (averages.isPriceCloseToPeak(data, symbol))) {
                liquidate(symbol);
                return TOOK_PROFITS;
            }

            if (hasHighVolatility(lastVix) ||
                    averages.did10DayMACrossBelow21DayMA() ||
                    averages.isPriceCloseToPeak(data, symbol)) {
                liquidate(symbol);
                return READY_TO_BUY;
            }
            return WE_HOLD_POSITIONS;
        }
    };
    public final ProfitState BOUGHT_ABOVE_50 = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            logSellAction(data, averages, symbol, lastVix, portfolio);

            if ((!averages.priceBelow50DayMovingAverage(data, symbol) &&
                    !hasHighVolatility(lastVix) &&
                    !averages.did10DayMACrossBelow21DayMA())
                    && (averages.isPriceCloseToPeak(data, symbol))) {
                liquidate(symbol);
                return TOOK_PROFITS;
            }

            if (averages.priceBelow50DayMovingAverage(data, symbol) ||
                    hasHighVolatility(lastVix) ||
                    averages.did10DayMACrossBelow21DayMA() ||
                    averages.isPriceCloseToPeak(data, symbol)) {
                liquidate(symbol);
                return READY_TO_BUY;
            }
            return WE_HOLD_POSITIONS;
        }
    };

    public final ProfitState TOOK_PROFITS = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            if (data.get(symbol).getPrice() < averages.movingAverage10.getValue()) {
                return READY_TO_BUY;
            }
            return this;
        }
    };

    Averages averages;
    private ProfitState profitState = READY_TO_BUY;

    private abstract class ProfitState {
        public abstract ProfitState onData(Slice data, String symbol, Averages averages);
    }

    public static final double EXIT_THRESHOLD = 22.0;
    public static final double ENTRY_THRESHOLD = 19.0;
    String symbol = "TQQQ";
    double previousPrice;
    LocalDate previous;
    CBOE lastVix;

    public void initialize() {
        this.setStartDate(2010, 3, 23);  //Set Start Date
        this.setEndDate(2020, 03, 06);

        this.setCash(100000);             //Set Strategy Cash
        averages = new Averages(createAndAddSimpleMovingAverage(symbol, 200), createAndAddSimpleMovingAverage(symbol, 50), createAndAddSimpleMovingAverage(symbol, 21), createAndAddSimpleMovingAverage(symbol, 10));
    }

    protected void onData(Slice data) {
        if (previous == getDate()) {
            return;
        }

        if (!averages.movingAverage200.isReady()) {
            return;
        }

        if (data.get(symbol) == null) {
            this.log(String.format("No data for symbol %s", symbol));
            return;
        }

        if (data.getCBOE("VIX") != null) {
            lastVix = data.getCBOE("VIX");
        }

        setState(data);

        previous = getDate();
        averages.updatePreviousAverages();
        previousPrice = data.get(symbol).getPrice();
    }

    private void setState(Slice data) {
        profitState = profitState.onData(data, symbol, averages);
    }

    private void logSellAction(Slice data, Averages averages, String symbol, CBOE lastVix, Map<String, Holding> portfolio) {
        double close = lastVix.getClose();
        double change = (data.get(symbol).getPrice() - portfolio.get(symbol).getAveragePrice()) / portfolio.get(symbol).getAveragePrice();

        if (averages.priceBelow50DayMovingAverage(data, symbol)) {
            log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (hasHighVolatility(lastVix)) {
            log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (averages.did10DayMACrossBelow21DayMA()) {
            log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (averages.isPriceCloseToPeak(data, symbol)) {
            log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, close));
        }
    }

    private boolean hasHighVolatility(CBOE lastVix) {
        return lastVix.getClose() > EXIT_THRESHOLD;
    }

    private boolean hasLowVolatility(CBOE lastVix) {
        return lastVix.getClose() < ENTRY_THRESHOLD;
    }

}