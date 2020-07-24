package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;
import com.turleylabs.algo.trader.kata.states.*;

import java.time.LocalDate;
import java.util.Map;

public class RefactorMeAlgorithm extends BaseAlgorithm implements RefactorMeLogger {
    public static final double USE_ALL_FUNDS = 1.0;
    public static final String VIX = "VIX";

    public final ProfitState READY_TO_BUY = new ReadyToBuy(this, symbol1 -> setHoldings(symbol1, USE_ALL_FUNDS), logLine1 -> log2(logLine1), this, new ShouldBuy());
    public final ProfitState BOUGHT_BELOW_50 = new WeHoldPositions(this, this::liquidate, RefactorMeAlgorithm.this.portfolio, this);
    public final ProfitState BOUGHT_ABOVE_50 = new BoughtAbove50(this, this::liquidate, RefactorMeAlgorithm.this.portfolio, this);
    public final ProfitState TOOK_PROFITS = new TookProfits(this);

    Averages averages;
    private ProfitState profitState = READY_TO_BUY;

    public static final double EXIT_THRESHOLD = 22.0;
    public static final double ENTRY_THRESHOLD = 19.0;
    String symbol = "TQQQ";
    double previousPrice;
    LocalDate previous;
    public CBOE lastVix;

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

        if (data.getCBOE(VIX) != null) {
            lastVix = data.getCBOE(VIX);
        }

        setState(data);

        previous = getDate();
        averages.updatePreviousAverages();
        previousPrice = data.get(symbol).getPrice();
    }

    private void setState(Slice data) {
        profitState = profitState.onData(data, symbol, averages, lastVix.getClose());
    }

    @Override
    public void logSellAction(Slice data, Averages averages, String symbol, CBOE lastVix, Map<String, Holding> portfolio) {
        double close = lastVix.getClose();
        double change = (data.get(symbol).getPrice() - portfolio.get(symbol).getAveragePrice()) / portfolio.get(symbol).getAveragePrice();

        if (averages.priceBelow50DayMAByAtLeast(data, symbol, .07)) {
            log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (hasHighVolatility(lastVix)) {
            log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (averages.did10DayMACrossBelow21DayMA()) {
            log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, close));
        } else if (averages.isPriceCloseToPeak(data, symbol)) {
            log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, close));
        }
    }

    public boolean hasHighVolatility(CBOE lastVix) {
        return lastVix.getClose() > EXIT_THRESHOLD;
    }

    @Override
    public void log2(String logline) {
        log(logline);
    }
}