package com.matheus.financeai.application;

import com.matheus.financeai.domain.Category;
import com.matheus.financeai.domain.Transaction;
import com.matheus.financeai.domain.TransactionRepository;
import com.matheus.financeai.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetFinancialSummaryUseCaseTest {
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final GetFinancialSummaryUseCase useCase = new GetFinancialSummaryUseCase(transactionRepository);

    @Test
    void shouldCalculateIncomeExpensesAndBalance() {
        when(transactionRepository.findAll()).thenReturn(List.of(
                new Transaction("Salário", 10_000, Category.OTHER, TransactionType.INCOME),
                new Transaction("Mercado", 2_500, Category.GROCERIES, TransactionType.EXPENSE),
                new Transaction("Farmácia", 1_250, Category.PHARMA, TransactionType.EXPENSE)
        ));

        var result = useCase.execute();

        assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.totalExpenses()).isEqualByComparingTo(new BigDecimal("37.50"));
        assertThat(result.balance()).isEqualByComparingTo(new BigDecimal("62.50"));
    }

    @Test
    void shouldExposeFinancialSummaryAsTool() throws NoSuchMethodException {
        var annotation = GetFinancialSummaryUseCase.class
                .getMethod("execute")
                .getAnnotation(Tool.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.name()).isEqualTo("get-financial-summary");
    }
}
