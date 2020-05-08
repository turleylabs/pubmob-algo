package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.Slice;
import org.approvaltests.ApprovalUtilities;
import org.approvaltests.Approvals;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Locale;

import static org.junit.Assert.assertFalse;

public class AlgorithmTest {

    @Test
    public void onData_no_trades_do_not_take_profits() {
        RefactorMeAlgorithm refactorAlgorithm = new RefactorMeAlgorithm();

        refactorAlgorithm.onData(new Slice(LocalDate.now()));

        assertFalse(refactorAlgorithm.tookProfits);
    }

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

}
