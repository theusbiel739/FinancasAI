package com.matheus.financeai.application;

import com.matheus.financeai.application.output.FinancialSummaryOutput;
import com.matheus.financeai.domain.FinancialSummary;
import com.matheus.financeai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class GetFinancialSummaryUseCase {
    private final TransactionRepository transactionRepository;

    public GetFinancialSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-financial-summary",
            description = "Informa o total de receitas, o total de despesas e o saldo financeiro")
    public FinancialSummaryOutput execute() {
        var summary = FinancialSummary.from(transactionRepository.findAll());
        return FinancialSummaryOutput.from(summary);
    }
}
