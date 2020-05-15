package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;

import java.time.LocalDate;

public class RefactorMeAlgorithm extends BaseAlgorithm {

    public static final double EXIT_THRESHOLD = 22.0;
    public static final double ENTRY_THRESHOLD = 19.0;
    String symbol = "TQQQ";
    SimpleMovingAverage movingAverage200;
    SimpleMovingAverage movingAverage50;
    SimpleMovingAverage movingAverage21;
    SimpleMovingAverage movingAverage10;
    double previousMovingAverage50;
    double previousMovingAverage21;
    double previousMovingAverage10;
    double previousPrice;
    LocalDate previous;
    CBOE lastVix;
    boolean boughtBelow50DayMovingAverage;
    boolean tookProfits;

    public void initialize() {
        this.setStartDate(2010, 3, 23);  //Set Start Date
        this.setEndDate(2020, 03, 06);

        this.setCash(100000);             //Set Strategy Cash

        movingAverage200 = this.createAndAddSimpleMovingAverage(symbol, 200);
        movingAverage50 = this.createAndAddSimpleMovingAverage(symbol, 50);
        movingAverage21 = this.createAndAddSimpleMovingAverage(symbol, 21);
        movingAverage10 = this.createAndAddSimpleMovingAverage(symbol, 10);
    }

    protected void onData(Slice data) {
        if (previous == getDate()) return;

        if (!movingAverage200.isReady()) return;

        if (data.get(symbol) == null) {
            this.log(String.format("No data for symbol %s", symbol));
            return;
        }

        if (data.getCBOE("VIX") != null) {
            lastVix = data.getCBOE("VIX");
        }

        double movingAverage10Value = movingAverage10.getValue();
        double movingAverage21Value = movingAverage21.getValue();
        double movingAverage50Value = movingAverage50.getValue();
        double movingAverage200Value = movingAverage200.getValue();

        if (tookProfits) {
            if (data.get(symbol).getPrice() < movingAverage10Value) {
                tookProfits = false;
            }
        } else {
            if (doWeHold(symbol))
                sellIfNecessary(data, movingAverage10Value, movingAverage21Value, movingAverage50Value, movingAverage200Value);
            else
                buyIfNecessary(data, movingAverage10Value, movingAverage21Value, movingAverage50Value, movingAverage200Value);
        }

        previous = getDate();
        previousMovingAverage50 = movingAverage50Value;
        previousMovingAverage21 = movingAverage21Value;
        previousMovingAverage10 = movingAverage10Value;
        previousPrice = data.get(symbol).getPrice();
    }

    private boolean doWeHold(String symbol) {
        return portfolio.getOrDefault(symbol, Holding.Default).getQuantity() > 0;
    }

    private void sellIfNecessary(Slice data, double movingAverage10Value, double movingAverage21Value, double movingAverage50Value, double movingAverage200Value) {
        double change = (data.get(symbol).getPrice() - portfolio.get(symbol).getAveragePrice()) / portfolio.get(symbol).getAveragePrice();

        if (didSomethingAroundTriggerToSell(data)) {
            this.log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
        } else if (hasHighVolatility()) {
            this.log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
        } else if (did10DayMACrossBelow21DayMA(movingAverage10Value, movingAverage21Value)) {
            this.log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
        } else if (isPriceCloseToPeak(data, movingAverage50Value, movingAverage200Value)) {
            this.log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
        }

        if (!notReallySelling(data, movingAverage10Value, movingAverage21Value)
                && (isPriceCloseToPeak(data, movingAverage50Value, movingAverage200Value))) {
            tookProfits = true;
        }

        if (shouldLiquidate(data, movingAverage50Value, movingAverage10Value, movingAverage21Value, movingAverage200Value))
            this.liquidate(symbol);
    }

    private boolean notReallySelling(Slice data, double movingAverage10Value, double movingAverage21Value) {
        return didSomethingAroundTriggerToSell(data) ||
                hasHighVolatility() ||
                did10DayMACrossBelow21DayMA(movingAverage10Value, movingAverage21Value);
    }

    private boolean shouldLiquidate(Slice data, double movingAverage50Value, double movingAverage10Value, double movingAverage21Value, double movingAverage200Value) {
        return notReallySelling(data, movingAverage10Value, movingAverage21Value)
                || isPriceCloseToPeak(data, movingAverage50Value, movingAverage200Value);
    }

    private boolean didSomethingAroundTriggerToSell(Slice data) {
        boolean boughtAbove50DayMA = !boughtBelow50DayMovingAverage;
        return data.get(symbol).getPrice() < (movingAverage50.getValue() * .93) && boughtAbove50DayMA;
    }

    private boolean isPriceNotCloseToPeak(Slice data, double movingAverage50Value, double movingAverage200Value) {
        return !(isPriceCloseToPeak(data, movingAverage50Value, movingAverage200Value));
    }

    private boolean isPriceCloseToPeak(Slice data, double movingAverage50Value, double movingAverage200Value) {
        return data.get(symbol).getPrice() >= (movingAverage50Value * 1.15)
                && data.get(symbol).getPrice() >= (movingAverage200Value * 1.40);
    }

    private boolean did10DayMACrossBelow21DayMA(double movingAverage10Value, double movingAverage21Value) {
        return movingAverage10Value < 0.97 * movingAverage21Value;
    }

    private void buyIfNecessary(Slice data, double movingAverage10Value, double movingAverage21Value, double movingAverage50Value, double movingAverage200Value) {
        if (arePricesRisingNearTerm(data, movingAverage10Value, movingAverage21Value)
                && isPriceNotCloseToPeak(data, movingAverage50Value, movingAverage200Value)
                && hasLowVolatility()
                && isPriceNearShortTermAverage(data, movingAverage10Value)) {
            this.log(String.format("Buy %s Vix %.4f. above 10 MA %.4f", symbol, lastVix.getClose(), (data.get(symbol).getPrice() - movingAverage10Value) / movingAverage10Value));
            double amount = 1.0;
            this.setHoldings(symbol, amount);

            boughtBelow50DayMovingAverage = data.get(symbol).getPrice() < movingAverage50Value;
        }
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

    private boolean arePricesRisingNearTerm(Slice data, double movingAverage10Value, double movingAverage21Value) {
        return data.get(symbol).getPrice() > movingAverage10Value
                && movingAverage10Value > movingAverage21Value
                && movingAverage10Value > previousMovingAverage10
                && movingAverage21Value > previousMovingAverage21;
    }

}
