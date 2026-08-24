package com.matheus.financeai.application;

import com.matheus.financeai.application.input.PersistTransactionInput;
import com.matheus.financeai.application.output.TransactionOutput;
import com.matheus.financeai.domain.Transaction;
import com.matheus.financeai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input) {
        var transaction = transactionRepository.save(
                new Transaction(input.description(), input.amount(), input.category(), input.type()));

        return TransactionOutput.from(transaction);
    }
}
