package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;

import java.time.LocalDate;

public class RefactorMeAlgorithm extends BaseAlgorithm {

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
    boolean boughtBelow50;
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
            if (isaBuy()) {
                buyCalculation(data, movingAverage10Value, movingAverage21Value, movingAverage50Value, movingAverage200Value);
            } else {
                sellCalculation(data, movingAverage10Value, movingAverage21Value, movingAverage50Value, movingAverage200Value);
            }
        }

        previous = getDate();
        previousMovingAverage50 = movingAverage50Value;
        previousMovingAverage21 = movingAverage21Value;
        previousMovingAverage10 = movingAverage10Value;
        previousPrice = data.get(symbol).getPrice();
    }

    private boolean isaBuy() {
        return portfolio.getOrDefault(symbol, Holding.Default).getQuantity() == 0;
    }

    private void sellCalculation(Slice data, double movingAverage10Value, double movingAverage21Value, double movingAverage50Value, double movingAverage200Value) {
        double change = (data.get(symbol).getPrice() - portfolio.get(symbol).getAveragePrice()) / portfolio.get(symbol).getAveragePrice();

        if (data.get(symbol).getPrice() < (movingAverage50Value * .93) && !boughtBelow50) {
            this.log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
            this.liquidate(symbol);
        } else {
            if ((double) (lastVix.getClose()) > 22.0) {
                this.log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                this.liquidate(symbol);
            } else {
                if (movingAverage10Value < 0.97 * movingAverage21Value) {
                    this.log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                    this.liquidate(symbol);
                } else {
                    if (data.get(symbol).getPrice() >= (movingAverage50Value * 1.15) && data.get(symbol).getPrice() >= (movingAverage200Value * 1.40)) {
                        this.log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                        this.liquidate(symbol);
                        tookProfits = true;
                    }
                }
            }
        }
    }

    private void buyCalculation(Slice data, double movingAverage10Value, double movingAverage21Value, double movingAverage50Value, double movingAverage200Value) {
        if (data.get(symbol).getPrice() > movingAverage10Value
                && movingAverage10Value > movingAverage21Value
                && movingAverage10Value > previousMovingAverage10
                && movingAverage21Value > previousMovingAverage21
                && (double) (lastVix.getClose()) < 19.0
                && !(data.get(symbol).getPrice() >= (movingAverage50Value * 1.15) && data.get(symbol).getPrice() >= (movingAverage200Value * 1.40))
                && (data.get(symbol).getPrice() - movingAverage10Value) / movingAverage10Value < 0.07) {
            this.log(String.format("Buy %s Vix %.4f. above 10 MA %.4f", symbol, lastVix.getClose(), (data.get(symbol).getPrice() - movingAverage10Value) / movingAverage10Value));
            double amount = 1.0;
            this.setHoldings(symbol, amount);

            boughtBelow50 = data.get(symbol).getPrice() < movingAverage50Value;
        }
    }

}
