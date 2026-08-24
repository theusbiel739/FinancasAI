package com.matheus.financeai.infrastructure.http.request;

import com.matheus.financeai.application.input.PersistTransactionInput;
import com.matheus.financeai.domain.Category;
import com.matheus.financeai.domain.TransactionType;

public record TransactionRequest(String description, Category category, long amount, TransactionType type) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, amount, category, type);
    }
}
