---
inclusion: always
---

# Project Structure & Conventions

## Organização de Diretórios

```
serverest_restassured_java/
│
├── .kiro/                                      # Steering & Specs (governança Kiro)
│   ├── steering/                               # Diretrizes de como fazer código
│   │   ├── product.md                          # Propósito, personas, objetivos
│   │   ├── tech.md                             # Stack tecnológica, restrições
│   │   ├── structure.md                        # Organização (este arquivo)
│   │   ├── qa-global-dod.md                    # Definição de Pronto de Qualidade
│   │   └── api-rest-assured.md                 # Regras de automação Rest Assured
│   │
│   └── specs/                                  # Especificações de features
│       ├── authentication/
│       │   ├── requirements.md                 # Requisitos EARS de login
│       │   └── tasks.md                        # Checklist de implementação
│       ├── users-management/
│       │   ├── requirements.md
│       │   └── tasks.md
│       ├── cart-checkout/
│       │   ├── requirements.md
│       │   └── tasks.md
│       └── products/
│           ├── requirements.md
│           └── tasks.md
│
├── src/test/java/dev/serverest/                # Código-fonte dos testes
│   ├── BaseApiTest.java                        # Classe base com RequestSpec + AllureRestAssured
│   ├── login/
│   │   └── LoginFeatureTests.java              # Suíte de testes: autenticação
│   ├── usuarios/
│   │   └── UsersFeatureTests.java              # Suíte de testes: gestão de usuários
│   ├── carrinhos/
│   │   └── CartsFeatureTests.java              # Suíte de testes: carrinho de compras
│   ├── performance/
│   │   └── RateLimitLoadTests.java             # Testes NFR: carga com JMeter DSL
│   └── utils/
│       ├── FakerUtils.java                     # Geradores de dados dinâmicos
│       ├── TestDataBuilder.java                # (futuro) Builder para payloads complexos
│       └── JsonSchemaValidator.java            # (futuro) Helpers de validação
│
├── src/test/resources/                         # Dados e esquemas de teste
│   ├── schemas/
│   │   └── usuarios/
│   │       └── user-by-id-schema.json          # JSON Schema para validação de contrato
│   ├── restassured/
│   │   └── login/
│   │       └── invalid-login-emails.csv        # Massa de dados parametrizados
│   └── fixtures/
│       └── (futuro) users.json                 # Dados de teste estruturados
│
├── .github/workflows/                          # CI/CD (GitHub Actions)
│   └── rest-assured-api-pipeline.yml           # Pipeline: test + report + publish
│
├── pom.xml                                     # Dependências e plugins Maven
├── pmd-ruleset.xml                             # Regras PMD customizadas
├── checkstyle-suppressions.xml                 # Supressões do Checkstyle
├── docker-compose.serverest.yml                # Orquestração local: API ServeRest
│
├── Requisitos.md                               # Especificações de negócio do projeto
├── TESTING_API.MD                              # Mapeamento de cenários vs endpoints
├── SWAGGER_TESTS.md                            # Catálogo de testes do Swagger
├── Heuristicas.MD                              # Análise VADER de cobertura
├── RELEASE_NOTES.md                            # Histórico de versões
│
└── README.md                                   # Documentação principal
```

## Padrões de Nomenclatura

### Arquivos Java

```java
// Suítes de teste: <Recurso>FeatureTests.java
LoginFeatureTests.java
UsersFeatureTests.java
CartsFeatureTests.java
RateLimitLoadTests.java         // Testes NFR

// Classes utilitárias: <Propósito>Utils.java ou <Propósito>Helper.java
FakerUtils.java
JsonSchemaValidator.java
```

### Métodos de Teste

```java
// Padrão: <verbo><ação><cenário>
// Exemplo:
void loginWithValidCredentials()
void getLastNOnPublicProducts()
void deleteCartWithValidToken()
void shouldReturnConflictWhenEmailExists()
void validateRateLimitingOn3ConsecutiveFailures()
```

### Constantes e Dados

```java
// Chaves de payload (definidas em BaseApiTest): UPPER_SNAKE_CASE
public static final String KEY_ID = "_id";
public static final String KEY_NOME = "nome";
public static final String KEY_EMAIL = "email";
public static final String KEY_PASSWORD = "password";
public static final String KEY_ADMINISTRADOR = "administrador";
public static final String KEY_MESSAGE = "message";

// Rotas: ROTA_<RECURSO>
public static final String ROTA_USUARIOS = "/usuarios";
public static final String ROTA_LOGIN = "/login";
public static final String ROTA_CARRINHOS = "/carrinhos";
public static final String ROTA_PRODUTOS = "/produtos";
public static final String ROTA_CANCELAR_COMPRA = "/carrinhos/cancelar-compra";
```

### Dados de Teste

```
CSV: kebab-case com sufixo .csv
  invalid-login-emails.csv

JSON Schema: kebab-case com prefixo de recurso
  user-by-id-schema.json
  cart-items-schema.json

Markdown: kebab-case
  requirements.md
  tasks.md
```

## Convenções de Importação

```java
// Ordem padrão:
// 1. java.* (JDK)
import java.util.*;
import java.time.*;

// 2. Bibliotecas externas
import io.restassured.*;
import org.junit.jupiter.api.*;
import io.qameta.allure.*;

// 3. Projeto
import dev.serverest.BaseApiTest;
import dev.serverest.utils.*;
```

## Organização de Pacotes Java

```
dev.serverest                           # Raiz do projeto
├── BaseApiTest                         # Classe base, configurações globais
├── login                               # Feature: autenticação
├── usuarios                            # Feature: gestão de usuários
├── carrinhos                           # Feature: carrinho + checkout
├── performance                         # NFR: testes de carga
└── utils                               # Utilitários compartilhados
```

## Estrutura de um Teste

```java
package dev.serverest.usuarios;

import static org.hamcrest.Matchers.equalTo;

import dev.serverest.BaseApiTest;
import dev.serverest.utils.FakerUtils;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UsersFeatureTests extends BaseApiTest {
    
    @Test
    @DisplayName("CT01 - Criar usuário com dados válidos")
    void createUserWithValidData() {
        // ARRANGE: Preparar dados
        final String userEmail = FakerUtils.randomEmail();
        final String userName = FakerUtils.randomName();
        
        // ACT: Executar ação
        givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_USUARIOS)
            .body(bodyPayload(userName, userEmail, DEFAULT_PASSWORD, "false"))
        .when()
            .post()
        .then()
            // ASSERT: Validar resultado
            .statusCode(201)
            .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"));
    }
}
```

## Fronteiras Arquiteturais

Referências diretas ao código-fonte base:
- #[[file:src/test/java/dev/serverest/BaseApiTest.java]]
- #[[file:src/test/java/dev/serverest/utils/FakerUtils.java]]

### Camada: Request Specification (`BaseApiTest`)

```java
protected RequestSpecification givenWithAllure() {
    return RestAssured.given().filter(new AllureRestAssured());
}
```

→ **Todos os testes** herdam via `extends BaseApiTest`, chamam `givenWithAllure()` e definem `.contentType(ContentType.JSON)` quando enviam payload.

### Camada: Utilitários de Dados (`FakerUtils`)

```java
public class FakerUtils {
    public static String randomName() { /* ... */ }
    public static String randomProduct() { /* ... */ }
    public static String randomEmail() { /* ... */ }
    public static String randomPassword() { /* ... */ }
}
```

→ **Usar para dados dinâmicos** em payloads de teste. Evita colisões de dados em execuções paralelas.

### Camada: Validação de Schema (`src/test/resources/schemas/`)

```json
// user-by-id-schema.json
{
  "type": "object",
  "properties": {
    "nome": { "type": "string" },
    "email": { "type": "string", "format": "email" },
    "password": { "type": "string" },
    "administrador": { "type": "string" },
    "_id": { "type": "string" }
  },
  "required": ["nome", "email", "password", "administrador", "_id"]
}
```

→ **Usar em testes** via `matchesJsonSchemaInClasspath("schemas/usuarios/user-by-id-schema.json")`.

## Isolamento de Estado

- ✅ **Cada teste é independente**: não reutiliza dados de testes anteriores.
- ✅ **Dados únicos por execução**: `FakerUtils.randomEmail()` gera novo e-mail a cada chamada (com `System.nanoTime()`).
- ✅ **Execução Paralela Segura**: Suporte a `@Execution(ExecutionMode.CONCURRENT)` sem estado mutável compartilhado.
- ✅ **Transações de teste**: Cada `POST` (Create), `PUT` (Update), `DELETE` cria sua própria massa de dados.

## Inclusão de Recursos (Kiro Steering)

Conforme a documentação oficial (https://kiro.dev/docs/steering/):

### Arquivos com `inclusion: always`
Incluídos **automaticamente** em todas as interações e sessões:
- `product.md` (Foundational: visão do produto)
- `tech.md` (Foundational: stack e ferramentas)
- `structure.md` (Foundational: estrutura e convenções)
- `qa-global-dod.md` (Governança global de qualidade e DoD)

### Arquivos com `inclusion: fileMatch` (`fileMatchPattern`)
Incluídos **dinamicamente** quando arquivos correspondentes ao glob são lidos ou editados:
- `api-rest-assured.md` → ativado via `fileMatchPattern` em `src/test/java/dev/serverest/**/*.java` e `src/test/resources/**/*`

### Arquivos de Especificação (`.kiro/specs/`)
Documentação estruturada de requisitos e tarefas por feature:
- Specs em `.kiro/specs/<feature>/requirements.md` e `tasks.md`
