package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;

import java.time.LocalDate;

public class RefactorMeAlgorithm extends BaseAlgorithm {

    public final ProfitState DID_NOT_TAKE_PROFITS = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            if (arePricesRisingNearTerm(data, averages)
                    && isPriceNotCloseToPeak(data)
                    && hasLowVolatility()
                    && isPriceNearShortTermAverage(data, averages.movingAverage10.getValue())) {
                RefactorMeAlgorithm.this.log(String.format("Buy %s Vix %.4f. above 10 MA %.4f", RefactorMeAlgorithm.this.symbol, lastVix.getClose(), (data.get(RefactorMeAlgorithm.this.symbol).getPrice() - averages.movingAverage10.getValue()) / averages.movingAverage10.getValue()
                ));
                double amount = 1.0;
                RefactorMeAlgorithm.this.setHoldings(RefactorMeAlgorithm.this.symbol, amount);

                boughtBelow50DayMovingAverage = data.get(RefactorMeAlgorithm.this.symbol).getPrice() < averages.movingAverage50.getValue();
                return WE_HOLD;
            }
            return DID_NOT_TAKE_PROFITS;
        }
    };
    public final ProfitState WE_HOLD = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            logSellAction(data, averages);

            if (!notReallySelling(data, averages)
                    && (isPriceCloseToPeak(data, averages))) {
                liquidate(symbol);
                return TOOK_PROFITS;
            }

            if (shouldLiquidate(data)) {
                liquidate(symbol);
                return DID_NOT_TAKE_PROFITS;
            }
            return WE_HOLD;
        }
    };
    public final ProfitState TOOK_PROFITS = new ProfitState() {
        @Override
        public ProfitState onData(Slice data, String symbol, Averages averages) {
            if (data.get(symbol).getPrice() < averages.movingAverage10.getValue()) {
                return DID_NOT_TAKE_PROFITS;
            }
            return this;
        }
    };

    Averages averages;
    private ProfitState profitState = DID_NOT_TAKE_PROFITS;

    private abstract class ProfitState {
        public abstract ProfitState onData(Slice data, String symbol, Averages averages);
    }

    public static final double EXIT_THRESHOLD = 22.0;
    public static final double ENTRY_THRESHOLD = 19.0;
    String symbol = "TQQQ";
    double previousPrice;
    LocalDate previous;
    CBOE lastVix;
    boolean boughtBelow50DayMovingAverage;

    public void initialize() {
        this.setStartDate(2010, 3, 23);  //Set Start Date
        this.setEndDate(2020, 03, 06);

        this.setCash(100000);             //Set Strategy Cash
        averages = new Averages(symbol);
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

    private boolean doWeHold(String symbol) {
        return portfolio.getOrDefault(symbol, Holding.Default).getQuantity() > 0;
    }

    private void logSellAction(Slice data, Averages averages) {
        smallStep(data, averages, data.get(symbol), portfolio.get(symbol), portfolio.get(symbol), symbol, lastVix.getClose());
    }

    private void smallStep(Slice data, Averages averages, Bar bar, Holding holding, Holding holding1, String symbol, double close) {
        double change = (bar.getPrice() - holding.getAveragePrice()) / holding1.getAveragePrice();

        if (didSomethingAroundTriggerToSell(data)) {
            this.log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (hasHighVolatility()) {
            this.log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (did10DayMACrossBelow21DayMA(averages)) {
            this.log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (isPriceCloseToPeak(data, averages)) {
            this.log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, close));
        }
    }

    private boolean notReallySelling(Slice data, Averages averages) {
        return didSomethingAroundTriggerToSell(data) ||
                hasHighVolatility() ||
                did10DayMACrossBelow21DayMA(averages);
    }

    private boolean shouldLiquidate(Slice data) {
        return notReallySelling(data, averages)
                || isPriceCloseToPeak(data, averages);
    }

    private boolean didSomethingAroundTriggerToSell(Slice data) {
        boolean boughtAbove50DayMA = !boughtBelow50DayMovingAverage;
        return data.get(symbol).getPrice() < (averages.movingAverage50.getValue() * .93) && boughtAbove50DayMA;
    }

    private boolean isPriceNotCloseToPeak(Slice data) {
        return !(isPriceCloseToPeak(data, averages));
    }

    private boolean isPriceCloseToPeak(Slice data, Averages averages) {
        return data.get(symbol).getPrice() >= (averages.movingAverage50.getValue() * 1.15)
                && data.get(symbol).getPrice() >= (averages.movingAverage200.getValue() * 1.40);
    }

    private boolean did10DayMACrossBelow21DayMA(Averages averages) {
        return averages.movingAverage10.getValue() < 0.97 * averages.movingAverage21.getValue();
    }

    private boolean hasHighVolatility() {
        return lastVix.getClose() > EXIT_THRESHOLD;
    }

    private boolean hasLowVolatility() {
        return lastVix.getClose() < ENTRY_THRESHOLD;
    }

    private boolean isPriceNearShortTermAverage(Slice data, double movingAverage10Value) {
        return (data.get(symbol).getPrice() - movingAverage10Value) / movingAverage10Value < 0.07;
    }

    private boolean arePricesRisingNearTerm(Slice data, Averages averages) {
        return data.get(symbol).getPrice() > averages.movingAverage10.getValue()
                && averages.movingAverage10.getValue() > averages.movingAverage21.getValue()
                && averages.movingAverage10.getValue() > averages.previousMovingAverage10
                && averages.movingAverage21.getValue() > averages.previousMovingAverage21;
    }

    public class Averages {
        SimpleMovingAverage movingAverage200;
        SimpleMovingAverage movingAverage50;
        SimpleMovingAverage movingAverage21;
        SimpleMovingAverage movingAverage10;
        double previousMovingAverage50;
        double previousMovingAverage21;
        double previousMovingAverage10;

        public Averages(String symbol) {
            movingAverage200 = createAndAddSimpleMovingAverage(symbol, 200);
            movingAverage50 = createAndAddSimpleMovingAverage(symbol, 50);
            movingAverage21 = createAndAddSimpleMovingAverage(symbol, 21);
            movingAverage10 = createAndAddSimpleMovingAverage(symbol, 10);
        }

        public void updatePreviousAverages() {
            previousMovingAverage50 = movingAverage50.getValue();
            previousMovingAverage21 = movingAverage21.getValue();
            previousMovingAverage10 = movingAverage10.getValue();
        }
    }
}