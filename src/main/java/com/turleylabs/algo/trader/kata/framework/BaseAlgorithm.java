package com.turleylabs.algo.trader.kata.framework;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAlgorithm {

    ArrayList<SimpleMovingAverage> movingAverages = new ArrayList<>();
    private LocalDate currentDate;
    private int cash;
    private LocalDate startDate;
    private LocalDate endDate;
    protected Map<String, Holding> portfolio = new HashMap<>();
    protected ArrayList<Trade> trades = new ArrayList<>();
    private Slice currentSlice;

    public BaseAlgorithm() {
        initialize();
    }

    protected abstract void initialize();


    public void processData(Slice data) {
        if (data.get("TQQQ") == null) {
            return;
        }

        currentDate = data.getDate();
        currentSlice = data;

        for (SimpleMovingAverage movingAverage : movingAverages) {
            movingAverage.addData(data.get(movingAverage.getSymbol()).getPrice());
        }

        this.onData(data);

    }

    protected abstract void onData(Slice data);

    protected void setStartDate(int i, int i1, int i2) {
        this.startDate = LocalDate.of(i, i1, i2);
    }

    protected void setEndDate(int i, int i1, int i2) {
        this.endDate = LocalDate.of(i, i1, i2);
    }

    protected void setCash(int cash) {
        this.cash = cash;
    }

    public int getCash() {
        return cash;
    }

    protected LocalDate getDate() {
        return currentDate;
    }

    protected SimpleMovingAverage createAndAddSimpleMovingAverage(String symbol, int windowSize) {
        SimpleMovingAverage movingAverage = new SimpleMovingAverage(symbol, windowSize);
        this.movingAverages.add(movingAverage);
        return movingAverage;
    }

    protected void log(String log) {
        System.out.println(String.format("%tF - %s", currentDate, log));
    }

    protected void setHoldings(String symbol, double amount) {
        double averagePrice = currentSlice.get(symbol).getPrice();
        int shares = (int) ((amount * this.getCash()) / averagePrice);
        Trade trade = new Trade(symbol, currentDate, shares, averagePrice, 1);
        trades.add(trade);
        portfolio.put(symbol, new Holding(averagePrice, shares));
        this.cash -= (averagePrice * shares);
    }

    protected void liquidate(String symbol) {
        Holding holding = portfolio.get(symbol);
        int shares = holding.getQuantity();
        double currentPrice = currentSlice.get(symbol).getPrice();
        Trade trade = new Trade(symbol, currentDate, shares, currentPrice, -1);
        trades.add(trade);
        portfolio.remove(symbol);
        this.cash += (currentPrice * shares);
    }

    public void run() {
        startDate.datesUntil(endDate).forEach(date -> processData(new Slice(date)));
    }

}
