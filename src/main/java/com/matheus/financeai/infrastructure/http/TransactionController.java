package com.matheus.financeai.infrastructure.http;

import com.matheus.financeai.application.GetFinancialSummaryUseCase;
import com.matheus.financeai.application.ListTransactionsByCategoryUseCase;
import com.matheus.financeai.application.PersistTransactionUseCase;
import com.matheus.financeai.domain.Category;
import com.matheus.financeai.infrastructure.http.request.TransactionRequest;
import com.matheus.financeai.infrastructure.http.response.FinancialSummaryResponse;
import com.matheus.financeai.infrastructure.http.response.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final GetFinancialSummaryUseCase getFinancialSummaryUseCase;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 GetFinancialSummaryUseCase getFinancialSummaryUseCase) {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.getFinancialSummaryUseCase = getFinancialSummaryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/summary")
    public FinancialSummaryResponse readFinancialSummary() {
        return FinancialSummaryResponse.from(getFinancialSummaryUseCase.execute());
    }
}
