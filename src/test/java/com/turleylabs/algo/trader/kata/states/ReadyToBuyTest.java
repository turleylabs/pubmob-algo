package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.SimpleMovingAverage;
import com.turleylabs.algo.trader.kata.framework.Slice;
import org.junit.Test;

import java.time.LocalDate;
import java.util.function.Consumer;

public class ReadyToBuyTest {

  @Test
  public void basicBuyTest() {
    // arrange
    RefactorMeAlgorithm algorithm = new RefactorMeAlgorithm();
    Consumer<String> holdingsFunction = x -> {};
    Consumer<String> logFunction = x -> {};
    ReadyToBuy state = new ReadyToBuy(algorithm, holdingsFunction, logFunction, null);


    LocalDate tradeDate = null;
    Slice slice = new Slice(tradeDate);
    SimpleMovingAverage sma10 = new SimpleMovingAverage("TQQQ", 10);
    SimpleMovingAverage sma21 = new SimpleMovingAverage("TQQQ", 21);
    SimpleMovingAverage sma50 = new SimpleMovingAverage("TQQQ", 50);
    SimpleMovingAverage sma200 = new SimpleMovingAverage("TQQQ", 200);
    Averages averages = new Averages(sma200, sma50, sma21, sma10);

    state.onData(slice, "", averages);

    // assert
  }
}
