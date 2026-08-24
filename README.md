# Finance AI API

## Objetivo

Finance AI API é uma API de controle financeiro criada como projeto de portfólio para um desafio de Spring Boot e inteligência artificial. O projeto integra recursos de IA sem desviar das fronteiras entre domínio, aplicação e infraestrutura.

A aplicação recebe comandos de voz, transcreve o áudio, permite que o modelo escolha ferramentas da aplicação, persiste ou consulta transações no MySQL e devolve a resposta final em áudio.

As funcionalidades incluem cadastro, consulta por categoria e uma visão consolidada com:

- total de receitas;
- total de despesas;
- saldo, calculado como receitas menos despesas.

## Arquitetura

O código utiliza uma arquitetura em camadas:

```text
src/main/java/com/matheus/financeai/
├── domain/          # entidades, valores, regras e contrato do repositório
├── application/     # casos de uso, entradas e saídas
└── infrastructure/  # HTTP, JPA, MySQL e integração com Spring AI
```

### Domínio

- `Transaction`: transação financeira expressa em centavos.
- `TransactionType`: classifica a transação como receita (`INCOME`) ou despesa (`EXPENSE`).
- `FinancialSummary`: calcula receitas, despesas e saldo.
- `TransactionRepository`: contrato de persistência utilizado pelos casos de uso.

Transações sem tipo são interpretadas como `EXPENSE`, mantendo compatibilidade com clientes que enviam apenas dados de gastos.

### Aplicação

- `PersistTransactionUseCase`: registra uma transação.
- `ListTransactionsByCategoryUseCase`: consulta transações por categoria.
- `GetFinancialSummaryUseCase`: consulta todas as transações e produz o resumo financeiro.

Os três casos de uso podem ser registrados como ferramentas do Spring AI. O resumo usa `@Tool(name = "get-financial-summary")`, portanto o modelo pode acioná-lo quando o usuário perguntar por totais ou saldo.

### Infraestrutura

- `TransactionController`: expõe a API REST e coordena o fluxo de voz.
- `JpaTransactionRepository`: adapta o contrato do domínio ao Spring Data JPA.
- `TransactionEntityRepository`: acessa o MySQL.
- `TransactionEntity`: converte entre persistência e domínio.

## Fluxo de IA

```text
Arquivo de áudio
      ↓
TranscriptionModel (OpenAI Whisper)
      ↓
ChatClient + prompt do sistema
      ↓
Tool Calling escolhe um caso de uso
      ↓
Repositório JPA / MySQL
      ↓
Resposta textual do modelo
      ↓
TextToSpeechModel (OpenAI)
      ↓
Arquivo MP3
```

Ferramentas disponíveis ao modelo:

- `persist-transaction`;
- `list-transactions-by-category`;
- `get-financial-summary`.

O prompt em `src/main/resources/prompts/system-message.st` orienta o modelo a classificar receitas e despesas e a utilizar a ferramenta de resumo para perguntas sobre totais e saldo.

## Endpoints

### Registrar uma transação

```http
POST /transactions
Content-Type: application/json
```

Exemplo de receita — o valor é informado em centavos:

```json
{
  "description": "Salário",
  "category": "OTHER",
  "amount": 500000,
  "type": "INCOME"
}
```

Exemplo de despesa:

```json
{
  "description": "Compras do mês",
  "category": "GROCERIES",
  "amount": 85000,
  "type": "EXPENSE"
}
```

Se `type` não for enviado, a transação será tratada como `EXPENSE`.

### Consultar por categoria

```http
GET /transactions/GROCERIES
```

Categorias disponíveis: `GROCERIES`, `PHARMA`, `AUTO` e `OTHER`.

### Consultar o resumo financeiro

```http
GET /transactions/summary
```

Exemplo de resposta, com valores em reais:

```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 850.00,
  "balance": 4150.00
}
```

### Enviar um comando de voz

```http
POST /transactions/ai
Content-Type: multipart/form-data
```

Envie o áudio no campo `file`. A resposta é um arquivo `audio.mp3`.

## Tecnologias

- Java 25;
- Spring Boot 4.0.5;
- Spring AI 2.0.0-M4;
- Spring Web;
- Spring Data JPA;
- MySQL 9.6;
- Docker Compose;
- Gradle Wrapper 9.4.1;
- JUnit 5, AssertJ e Mockito;
- modelos OpenAI para chat, transcrição e síntese de voz.

## Dependências externas

### Java

O `build.gradle` exige uma toolchain Java 25. Confirme com:

```bash
java -version
```

### MySQL e Docker

O arquivo `compose.yml` cria um MySQL na porta local `3307`. O suporte Docker Compose do Spring Boot inicia o serviço automaticamente durante `bootRun` quando Docker está instalado e ativo.

Verifique o ambiente com:

```bash
docker --version
docker compose version
```

### Credencial da OpenAI

O chat, a transcrição e a síntese de voz exigem uma chave válida:

```bash
export OPENAI_API_KEY="sua_chave"
```

Nunca grave a chave em `application.properties`, arquivos versionados, exemplos de código ou commits. Os testes de integração com OpenAI são ignorados quando `OPENAI_API_KEY` não está definida.

Modelos configurados em `application.properties`:

- chat: `gpt-4o-mini`;
- transcrição: `whisper-1`, em português;
- voz: `gpt-4o-mini-tts`, voz `nova`.

## Como executar

Com Java 25, Docker ativo e `OPENAI_API_KEY` configurada:

```bash
./gradlew bootRun
```

A API ficará disponível em `http://localhost:8080`.

## Testes

Para executar toda a suíte:

```bash
./gradlew test
```

A suíte completa precisa do MySQL iniciado pelo Docker. Os testes que chamam diretamente a OpenAI também precisam de uma chave válida e podem consumir créditos da conta.

Os testes unitários do resumo financeiro não acessam banco, Docker ou OpenAI:

```bash
./gradlew test --tests 'com.matheus.financeai.application.*'
```

Eles verificam:

- cálculo do total de receitas;
- cálculo do total de despesas;
- cálculo do saldo;
- exposição do caso de uso como ferramenta do Spring AI;
- compatibilidade do tipo padrão `EXPENSE`.

## Referências

- [DIO Spring Boot Learning Track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track) — referência de estudo sobre Spring Boot, arquitetura em camadas e Spring AI.
- [Documentação do Spring AI](https://docs.spring.io/spring-ai/reference/index.html)
- [ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Transcrição de áudio](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
- [Síntese de voz](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)
