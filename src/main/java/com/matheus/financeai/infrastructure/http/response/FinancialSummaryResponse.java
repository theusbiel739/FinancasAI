package com.matheus.financeai.infrastructure.http.response;

import com.matheus.financeai.application.output.FinancialSummaryOutput;

import java.math.BigDecimal;

public record FinancialSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance) {
    public static FinancialSummaryResponse from(FinancialSummaryOutput output) {
        return new FinancialSummaryResponse(output.totalIncome(), output.totalExpenses(), output.balance());
    }
}
