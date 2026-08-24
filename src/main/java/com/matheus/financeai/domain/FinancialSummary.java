package com.matheus.financeai.domain;

import java.util.List;

public record FinancialSummary(long totalIncome, long totalExpenses, long balance) {
    public static FinancialSummary from(List<Transaction> transactions) {
        long totalIncome = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                .mapToLong(Transaction::getAmount)
                .sum();

        long totalExpenses = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .mapToLong(Transaction::getAmount)
                .sum();

        return new FinancialSummary(totalIncome, totalExpenses, totalIncome - totalExpenses);
    }
}
