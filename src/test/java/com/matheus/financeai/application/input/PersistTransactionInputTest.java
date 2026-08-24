package com.matheus.financeai.application.input;

import com.matheus.financeai.domain.Category;
import com.matheus.financeai.domain.TransactionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistTransactionInputTest {
    @Test
    void shouldKeepExpenseAsDefaultForBackwardCompatibility() {
        var input = new PersistTransactionInput("Mercado", 5_000, Category.GROCERIES, null);

        assertThat(input.type()).isEqualTo(TransactionType.EXPENSE);
    }
}
