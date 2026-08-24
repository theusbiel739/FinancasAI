package com.matheus.financeai.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private TransactionType type;

    public Transaction(String description, long amount, Category category) {
        this(description, amount, category, TransactionType.EXPENSE);
    }

    public Transaction(String description, long amount, Category category, TransactionType type) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = type == null ? TransactionType.EXPENSE : type;
    }
}
