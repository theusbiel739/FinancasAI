package com.matheus.financeai.infrastructure.http;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!openai-audio")
@RequestMapping("/transactions/ai")
public class AudioUnavailableController {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ProblemDetail> audioUnavailable() {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "O áudio é opcional. Ative o perfil 'openai-audio' e configure OPENAI_API_KEY "
                        + "para usar transcrição e síntese de voz.");
        problem.setTitle("Recurso de áudio não configurado");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
    }
}
