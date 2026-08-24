package com.matheus.financeai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;

class OllamaChatClientIT {
    @Test
    void shouldReceiveResponseFromLocalModel() {
        OllamaTestSupport.requireAvailableOllama();
        var chatClient = ChatClient.builder(OllamaTestSupport.chatModel()).build();

        var response = chatClient.prompt()
                .user("Responda apenas com a palavra FUNCIONANDO.")
                .call()
                .content();

        assertThat(response).containsIgnoringCase("FUNCIONANDO");
    }
}
