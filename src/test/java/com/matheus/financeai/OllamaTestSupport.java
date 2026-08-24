package com.matheus.financeai;

import org.junit.jupiter.api.Assumptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class OllamaTestSupport {
    private static final String BASE_URL = System.getenv()
            .getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");
    private static final String MODEL = System.getenv()
            .getOrDefault("OLLAMA_MODEL", "qwen2.5:7b");

    private OllamaTestSupport() {
    }

    static void requireAvailableOllama() {
        Assumptions.assumeTrue(isAvailable(),
                "Ollama não está disponível em " + BASE_URL + "; teste de integração ignorado.");
    }

    static OllamaChatModel chatModel() {
        var ollamaApi = OllamaApi.builder().baseUrl(BASE_URL).build();
        var options = new OllamaChatOptions();
        options.setModel(MODEL);
        options.setTemperature(0.0);

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options)
                .build();
    }

    private static boolean isAvailable() {
        try (var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(1))
                .build()) {
            var request = HttpRequest.newBuilder(URI.create(BASE_URL + "/api/version"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (Exception ignored) {
            return false;
        }
    }
}
