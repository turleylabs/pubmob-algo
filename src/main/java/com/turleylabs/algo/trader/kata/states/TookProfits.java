package com.turleylabs.algo.trader.kata.states;

import com.turleylabs.algo.trader.kata.Averages;
import com.turleylabs.algo.trader.kata.RefactorMeAlgorithm;
import com.turleylabs.algo.trader.kata.framework.Slice;

public class TookProfits extends ProfitState {
    private final RefactorMeAlgorithm refactorMeAlgorithm;

    public TookProfits(RefactorMeAlgorithm refactorMeAlgorithm) {
        this.refactorMeAlgorithm = refactorMeAlgorithm;
    }

    @Override
    public ProfitState onData(Slice data, String symbol, Averages averages) {
        if (data.get(symbol).getPrice() < averages.movingAverage10.getValue()) {
            return refactorMeAlgorithm.READY_TO_BUY;
        }
        return this;
    }
}
