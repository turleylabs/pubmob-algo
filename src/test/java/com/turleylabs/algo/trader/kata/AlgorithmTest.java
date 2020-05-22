package com.turleylabs.algo.trader.kata;

import org.approvaltests.ApprovalUtilities;
import org.approvaltests.Approvals;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

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
}
