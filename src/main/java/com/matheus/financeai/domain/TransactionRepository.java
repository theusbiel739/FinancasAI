package com.matheus.financeai.domain;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    List<Transaction> findAll();
}
