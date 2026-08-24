# Finance AI API

## Objetivo

Finance AI API é uma API de controle financeiro criada como projeto de portfólio com Spring Boot e Spring AI. A aplicação registra receitas e despesas, consulta transações, calcula um resumo financeiro e permite conversar com um assistente local capaz de acionar os casos de uso do sistema por Tool Calling.

O provedor padrão de inteligência artificial é o Ollama. O fluxo principal funciona localmente e não exige `OPENAI_API_KEY` nem consumo de uma API paga.

Funcionalidades:

- cadastro de receitas e despesas;
- consulta por categoria;
- total de receitas, total de despesas e saldo;
- chat local com Spring AI e Ollama;
- Tool Calling conectado aos casos de uso da aplicação;
- persistência em MySQL;
- transcrição e síntese de voz opcionais.

## Arquitetura

O código utiliza arquitetura em camadas:

```text
src/main/java/com/matheus/financeai/
├── domain/          # entidades, valores, regras e contrato do repositório
├── application/     # casos de uso, entradas e saídas
└── infrastructure/  # HTTP, JPA, configuração de IA e integrações externas
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

Os três casos de uso são ferramentas do Spring AI:

- `persist-transaction`;
- `list-transactions-by-category`;
- `get-financial-summary`.

### Infraestrutura de IA

`FinanceAiChatConfiguration` cria o `ChatClient` com o prompt do sistema e registra os casos de uso como ferramentas. O código da aplicação depende das abstrações do Spring AI, enquanto o starter oficial do Ollama fornece o modelo local.

Fluxo padrão:

```text
Mensagem HTTP
      ↓
ChatClient do Spring AI
      ↓
Ollama em localhost:11434
      ↓
qwen2.5:7b
      ↓
Tool Calling, quando necessário
      ↓
Caso de uso → repositório → MySQL
```

Se o Ollama não estiver acessível, o endpoint de chat responde com HTTP `503` e orienta a verificar o serviço e o modelo configurado.

## Modelo local

O modelo padrão é `qwen2.5:7b`.

Motivos da escolha:

- suporte a Tool Calling;
- suporte multilíngue, incluindo português;
- bom equilíbrio entre qualidade e uso de memória;
- aproximadamente 4,7 GB no Ollama, adequado para desenvolvimento em Apple Silicon com 16 GB de RAM.

O modelo pode ser alterado sem modificar o código:

```bash
export OLLAMA_MODEL="llama3.2:3b"
```

Também é possível alterar a URL:

```bash
export OLLAMA_BASE_URL="http://localhost:11434"
```

## Requisitos

- Java 25;
- Ollama;
- modelo local `qwen2.5:7b` ou outro modelo compatível com ferramentas;
- Docker e MySQL para execução normal com persistência.

Não é necessária uma chave OpenAI para o chat local, Tool Calling ou operações REST financeiras.

## Preparar o Ollama

Instale o Ollama pelo [site oficial](https://ollama.com/download) e baixe o modelo:

```bash
ollama pull qwen2.5:7b
```

Confira os modelos disponíveis:

```bash
ollama list
```

Se o serviço não iniciar automaticamente:

```bash
ollama serve
```

Valide a execução local:

```bash
ollama run qwen2.5:7b
```

## MySQL com Docker

O arquivo `compose.yml` cria um MySQL na porta local `3307`. O suporte Docker Compose do Spring Boot inicia o serviço durante `bootRun` quando Docker está instalado e ativo.

```bash
docker --version
docker compose version
```

## Executar a aplicação

Com Java 25, Ollama e Docker ativos:

```bash
./gradlew bootRun
```

A API ficará disponível em `http://localhost:8080`.

## Endpoints

### Conversar com a IA local

```http
POST /transactions/ai/chat
Content-Type: application/json
```

```json
{
  "message": "Qual é meu saldo atual?"
}
```

Exemplo de resposta:

```json
{
  "response": "Seu saldo atual é de R$ 4.150,00.",
  "model": "qwen2.5:7b"
}
```

O modelo pode consultar o resumo, listar transações ou registrar uma nova transação usando as ferramentas disponíveis.

### Registrar uma transação

```http
POST /transactions
Content-Type: application/json
```

Exemplo de receita, com valor em centavos:

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

```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 850.00,
  "balance": 4150.00
}
```

## Áudio opcional

O Ollama fornece o modelo de chat, mas não implementa as abstrações de transcrição e síntese de voz utilizadas pelo projeto. Para preservar o fluxo de áudio, ele permanece disponível em um perfil opcional que utiliza OpenAI somente para entrada e saída de voz. O processamento textual e o Tool Calling continuam no Ollama.

O perfil de áudio não é carregado durante a execução normal. Para ativá-lo explicitamente:

```bash
export OPENAI_API_KEY="sua_chave"
SPRING_PROFILES_ACTIVE=openai-audio ./gradlew bootRun
```

Depois envie o arquivo no campo `file`:

```http
POST /transactions/ai
Content-Type: multipart/form-data
```

Sem o perfil, esse endpoint retorna HTTP `503` com uma explicação. Nunca grave a chave em arquivos versionados.

## Tecnologias

- Java 25;
- Spring Boot 4.0.5;
- Spring AI 2.0.0-M4;
- Spring AI Ollama;
- Spring Web;
- Spring Data JPA;
- MySQL 9.6;
- H2 exclusivamente para testes;
- Docker Compose;
- Gradle Wrapper 9.4.1;
- JUnit 5, AssertJ e Mockito;
- OpenAI opcional apenas para transcrição e síntese de voz.

## Testes

Testes unitários não precisam de Ollama, OpenAI, Docker ou MySQL:

```bash
./gradlew test \
  --tests 'com.matheus.financeai.application.*' \
  --tests 'com.matheus.financeai.infrastructure.http.AiControllerTest'
```

Eles validam cálculos financeiros, Tool Calling do caso de uso, compatibilidade de despesas, entradas inválidas e a resposta quando o Ollama está indisponível.

Testes de integração local com IA:

```bash
./gradlew test --tests 'com.matheus.financeai.Ollama*IT'
```

Esses testes usam o Ollama e são ignorados automaticamente quando o serviço não está acessível. Nenhum teste de IA exige API paga.

Suíte completa:

```bash
./gradlew clean test
```

O perfil `test` utiliza H2 em memória e desativa Docker Compose. Assim, o teste de contexto valida a inicialização completa do Spring sem depender de um MySQL externo. A configuração normal da aplicação continua usando MySQL.

Quando o Ollama está disponível, a suíte também valida comunicação real com `qwen2.5:7b` e Tool Calling. Se o serviço local estiver desligado, somente esses testes de integração são ignorados de maneira controlada.

## Referências de estudo

- [DIO Spring Boot Learning Track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track)
- [Spring AI — Ollama Chat](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Spring AI — ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Spring AI — Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Ollama — qwen2.5:7b](https://ollama.com/library/qwen2.5:7b)
