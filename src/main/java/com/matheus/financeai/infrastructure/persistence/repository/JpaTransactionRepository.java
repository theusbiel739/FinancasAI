package com.matheus.financeai.infrastructure.persistence.repository;

import com.matheus.financeai.domain.Category;
import com.matheus.financeai.domain.Transaction;
import com.matheus.financeai.domain.TransactionRepository;
import com.matheus.financeai.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAll() {
        return transactionEntityRepository.findAll()
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
