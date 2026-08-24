package com.matheus.financeai.infrastructure.http.response;

import com.matheus.financeai.application.output.TransactionOutput;

public record TransactionResponse(String id, String category, String description, String type, double amount) {
    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(output.id(), output.category(), output.description(), output.type(), output.value());
    }
}
