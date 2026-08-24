package com.matheus.financeai.infrastructure.http;

import com.matheus.financeai.infrastructure.http.request.ChatRequest;
import com.matheus.financeai.infrastructure.http.response.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.util.Locale;

@RestController
@RequestMapping("/transactions/ai")
public class AiController {
    private final ChatClient chatClient;
    private final String model;

    public AiController(ChatClient chatClient,
                        @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatClient = chatClient;
        this.model = model;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A mensagem para a IA deve ser informada.");
        }

        try {
            var response = chatClient.prompt()
                    .user(request.message())
                    .call()
                    .content();
            return new ChatResponse(response, model);
        } catch (RuntimeException exception) {
            if (isOllamaConnectionFailure(exception)) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Não foi possível acessar o Ollama. Confirme se o serviço está ativo "
                                + "e se o modelo '" + model + "' foi baixado.", exception);
            }
            throw exception;
        }
    }

    private boolean isOllamaConnectionFailure(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof ConnectException || current instanceof ResourceAccessException) {
                return true;
            }

            var message = current.getMessage();
            if (message != null) {
                var normalizedMessage = message.toLowerCase(Locale.ROOT);
                if (normalizedMessage.contains("connection refused")
                        || normalizedMessage.contains("failed to connect")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }
}
