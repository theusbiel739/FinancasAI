package com.matheus.financeai.infrastructure.ai;

import com.matheus.financeai.application.GetFinancialSummaryUseCase;
import com.matheus.financeai.application.ListTransactionsByCategoryUseCase;
import com.matheus.financeai.application.PersistTransactionUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FinanceAiChatConfiguration {
    @Bean
    ChatClient financeAiChatClient(ChatClient.Builder chatClientBuilder,
                                   PersistTransactionUseCase persistTransactionUseCase,
                                   ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                   GetFinancialSummaryUseCase getFinancialSummaryUseCase,
                                   @Value("classpath:prompts/system-message.st") Resource systemPrompt)
            throws IOException {
        return chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(StandardCharsets.UTF_8))
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase,
                        getFinancialSummaryUseCase)
                .build();
    }
}
