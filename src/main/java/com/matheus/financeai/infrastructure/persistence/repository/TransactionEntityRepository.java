package com.matheus.financeai.infrastructure.persistence.repository;

import com.matheus.financeai.domain.Category;
import com.matheus.financeai.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);

    List<TransactionEntity> findAll();
}
