package com.matheus.financeai.application.output;

import com.matheus.financeai.domain.FinancialSummary;

import java.math.BigDecimal;

public record FinancialSummaryOutput(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance) {
    public static FinancialSummaryOutput from(FinancialSummary summary) {
        return new FinancialSummaryOutput(
                toCurrency(summary.totalIncome()),
                toCurrency(summary.totalExpenses()),
                toCurrency(summary.balance()));
    }

    private static BigDecimal toCurrency(long amountInCents) {
        return BigDecimal.valueOf(amountInCents, 2);
    }
}
