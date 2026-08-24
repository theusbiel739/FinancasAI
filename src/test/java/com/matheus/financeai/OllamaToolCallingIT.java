package com.matheus.financeai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;

import static org.assertj.core.api.Assertions.assertThat;

class OllamaToolCallingIT {
    @Test
    void shouldUseLocalTool() {
        OllamaTestSupport.requireAvailableOllama();
        var chatClient = ChatClient.builder(OllamaTestSupport.chatModel())
                .defaultSystem("Você é um assistente matemático e deve usar as ferramentas disponíveis.")
                .defaultTools(new MathTools())
                .build();

        var response = chatClient.prompt()
                .user("Use a ferramenta para somar 17 e 25. Responda apenas com o resultado.")
                .call()
                .content();

        assertThat(response).contains("42");
    }

    static class MathTools {
        @Tool(description = "Soma dois números inteiros")
        public int sum(int firstNumber, int secondNumber) {
            return firstNumber + secondNumber;
        }
    }
}
