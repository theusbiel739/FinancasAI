package com.matheus.financeai.infrastructure.http;

import com.matheus.financeai.infrastructure.http.request.ChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiControllerTest {
    private static final String MODEL = "qwen2.5:7b";

    @Test
    void shouldRejectBlankMessage() {
        var controller = new AiController(mock(ChatClient.class), MODEL);

        assertThatThrownBy(() -> controller.chat(new ChatRequest(" ")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void shouldExplainWhenOllamaIsUnavailable() {
        var chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        when(chatClient.prompt().user(anyString()).call().content())
                .thenThrow(new ResourceAccessException("Connection refused"));
        var controller = new AiController(chatClient, MODEL);

        assertThatThrownBy(() -> controller.chat(new ChatRequest("Mostre meu resumo")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    assertThat(exception.getReason()).contains("Ollama", MODEL);
                });
    }
}
