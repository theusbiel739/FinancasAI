package com.matheus.financeai.application.input;

import com.matheus.financeai.domain.Category;
import com.matheus.financeai.domain.TransactionType;
import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(@ToolParam(description = "Descrição da transação") String description,
                                      @ToolParam(description = "Valor da transação (em centavos)") long amount,
                                      @ToolParam(description = "Categoria de uma transação") Category category,
                                      @ToolParam(description = "Tipo da transação: INCOME para receita ou EXPENSE para despesa") TransactionType type) {
    public PersistTransactionInput {
        type = type == null ? TransactionType.EXPENSE : type;
    }
}
