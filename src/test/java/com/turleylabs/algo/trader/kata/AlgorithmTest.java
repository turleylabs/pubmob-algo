package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.Bar;
import com.turleylabs.algo.trader.kata.framework.CBOE;
import com.turleylabs.algo.trader.kata.framework.Slice;
import com.turleylabs.algo.trader.kata.framework.Trade;
import org.approvaltests.ApprovalUtilities;
import org.approvaltests.Approvals;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AlgorithmTest {
    @Test
    public void approval_should_pass() {
        Locale.setDefault(Locale.CANADA);
        RefactorMeAlgorithm refactorAlgorithm = new RefactorMeAlgorithm() {
            @Override
            public void initialize() {
                super.initialize();
                super.setEndDate(2012, 03, 06);
            }
        };
        ByteArrayOutputStream out = new ApprovalUtilities().writeSystemOutToStringBuffer();

        refactorAlgorithm.run();

        Approvals.verify(out.toString());
    }

    class TestableSlice extends Slice {

        private final double price;
        private final double close;

        public TestableSlice(LocalDate tradeDate, double price, double close) {
            super(tradeDate);
            this.price = price;
            this.close = close;
        }

        @Override
        public Bar get(String symbol) {
            return new Bar(price);
        }

        @Override
        public CBOE getCBOE(String symbol) {
            return new CBOE(close);
        }
    }


    @Test
    public void doesSomething() {
        TestableAlgorithm refactorAlgorithm = new TestableAlgorithm();
        Slice slice = new TestableSlice(LocalDate.of(2000, 1, 1), 10.0, 20.0);
        refactorAlgorithm.onData(slice);


        //check that something was added to Trades list
        // add getter to Refactor me to expose trades
        assertThat(refactorAlgorithm.getTrades(), is(not(empty())));

    }

    public static class TestableAlgorithm extends RefactorMeAlgorithm {
        @Override
        public void initialize() {
            super.initialize();
            super.setEndDate(2012, 3, 6);
        }

        public ArrayList<Trade> getTrades() {
            return trades;
        }
    }
}
