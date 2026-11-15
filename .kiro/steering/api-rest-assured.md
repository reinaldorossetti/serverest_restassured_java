---
inclusion: fileMatch
fileMatchPattern:
  - "src/test/java/dev/serverest/**/*.java"
  - "src/test/resources/**/*"
---

# Diretrizes de Automação - Rest Assured & Java

## Referências Vivas do Projeto (Contexto Kiro)
- #[[file:src/test/java/dev/serverest/BaseApiTest.java]]
- #[[file:src/test/java/dev/serverest/utils/FakerUtils.java]]
- #[[file:src/test/resources/schemas/usuarios/user-by-id-schema.json]]
- #[[file:Heuristicas.MD]]
- #[[file:pmd-ruleset.xml]]

## 1. Herança & Configuração Base

### ✅ OBRIGATÓRIO: Estender `BaseApiTest`

Toda suíte de testes **deve estender** `BaseApiTest` para herdar configuração centralizada de `BaseURI`, `RequestSpecification` e `Allure`.

```java
package dev.serverest.usuarios;

import dev.serverest.BaseApiTest;
import org.junit.jupiter.api.Test;

public class UsersFeatureTests extends BaseApiTest {
    // Acesso automático a:
    // - RestAssured.baseURI configurado
    // - givenWithAllure() para RequestSpec com Allure
    // - Constantes: ROTA_USUARIOS, ROTA_LOGIN, etc.
}
```

### ✅ OBRIGATÓRIO: Usar `givenWithAllure()` em todos os testes

```java
givenWithAllure()
    .contentType(ContentType.JSON)
    .basePath(ROTA_USUARIOS)
    .body(payload)
.when()
    .post()
.then()
    .statusCode(201);
```

→ Isso ativa o filtro `AllureRestAssured()` para capturar requisição/resposta.

---

## 2. Estrutura de Teste (AAA Pattern)

Organize cada teste em **Arrange → Act → Assert**:

```java
@Test
@DisplayName("CT01 - Descrever o cenário")
void describeWhatIsBeingTested() {
    // ARRANGE: Preparar dados de entrada
    final String email = FakerUtils.randomEmail();
    final String password = "senha123";
    
    // ACT: Executar a ação
    final Response response = givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of("email", email, "password", password))
    .when()
        .post();
    
    // ASSERT: Validar resultado esperado
    response.then()
        .statusCode(200)
        .body("message", equalTo("Login realizado com sucesso"))
        .body("authorization", notNullValue());
}
```

---

## 3. Nomenclatura & Rastreabilidade

### ✅ DisplayName Obrigatório

```java
@Test
@DisplayName("CT01 - Login com credenciais válidas deve retornar token JWT")
void loginWithValidCredentialsReturnsToken() { }
```

→ Formato: `CT<número> - <descrição clara do cenário>`

### ✅ Nome de Método Descritivo

```java
// BOM: Descreve exatamente o que é testado
void loginWithValidCredentialsReturnsToken() { }
void createUserWithDuplicateEmailReturnsConflict() { }
void getCartWithInvalidTokenReturnsForbidden() { }

// RUIM: Genérico, sem semântica
void testLogin() { }
void test1() { }
void userCreation() { }
```

---

## 4. Gerenciamento de Dados de Teste

### ✅ OBRIGATÓRIO: Usar `FakerUtils` para Dados Dinâmicos

```java
// CORRETO: Dados dinâmicos geram valores únicos por execução
final String email = FakerUtils.randomEmail();
final String password = FakerUtils.randomPassword();
final String fullName = FakerUtils.randomName();

// Usando o helper herdado de BaseApiTest (chaves em português e administrador como String):
final Map<String, Object> payload = bodyPayload(fullName, email, password, "false");
```

→ Previne colisão de dados e permite isolamento entre testes paralelos (`@Execution(ExecutionMode.CONCURRENT)`).

### ❌ PROIBIDO: Hardcoding de Dados ou Contrato Incorreto

```java
// ERRADO: Email hardcoded causa conflito em execuções concorrentes ou repetidas
Map.of("email", "usuario@teste.com", ...)  // ❌

// ERRADO: Chaves em inglês ("name", "administrator") ou booleano nativo no campo administrador
Map.of("name", fullName, "administrator", false) // ❌ ServeRest exige "nome" e "administrador": "false" (String)

// ERRADO: IDs hardcoded não existem ou causam acoplamento
givenWithAllure().basePath("/usuarios/12345") ...     // ❌
```

### ✅ Constantes de Chaves de Payload (Herdadas de `BaseApiTest`)

```java
// BOM: Reutilizar constantes UPPER_SNAKE_CASE definidas em BaseApiTest
// KEY_ID ("_id"), KEY_NOME ("nome"), KEY_EMAIL ("email"),
// KEY_PASSWORD ("password"), KEY_ADMINISTRADOR ("administrador"), KEY_MESSAGE ("message")

final Map<String, Object> payload = Map.of(
    KEY_NOME, fullName,
    KEY_EMAIL, email,
    KEY_PASSWORD, password,
    KEY_ADMINISTRADOR, "false"
);
```

---

## 5. Construção de Requests

### ✅ OBRIGATÓRIO: Content-Type e Accept

```java
givenWithAllure()
    .contentType(ContentType.JSON)
    .accept(ContentType.JSON)
    .basePath(ROTA_USUARIOS)
    .body(payload)
.when()
    .post()
.then()
    .statusCode(201);
```

### ✅ Usar `Map` ou `List` para Payloads (Tipagem)

```java
// CORRETO: Tipado, seguro para refactor
final Map<String, Object> payload = Map.of(
    "email", "user@test.com",
    "password", "senha123"
);

// EVITAR: String JSON, difícil manter
givenWithAllure()
    .body("{\"email\":\"user@test.com\",\"password\":\"senha123\"}")  // ❌
```

### ✅ Parametrização com `@ParameterizedTest`

```java
@ParameterizedTest(name = "CT05 - Validar e-mail inválido: {0}")
@CsvFileSource(
    resources = "/restassured/login/invalid-login-emails.csv",
    numLinesToSkip = 1
)
void validateInvalidEmailFormats(String invalidEmail) {
    final Map<String, Object> payload = Map.of(
        KEY_EMAIL, invalidEmail,
        KEY_PASSWORD, "senha123"
    );
    
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(payload)
    .when()
        .post()
    .then()
        .statusCode(400)
        .body(KEY_EMAIL, notNullValue());
}
```

---

## 6. Assertions & Validações

### ✅ Validar Status Code Explicitamente

```java
.then()
    .statusCode(200)  // ✅ Sempre esperar status code explícito
```

### ✅ Validar Estrutura & Conteúdo

```java
.then()
    .statusCode(201)
    .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
    .body(KEY_ID, notNullValue())
    .body(KEY_EMAIL, equalTo(email));
```

### ✅ Validar JSON Schema (Contrato)

```java
// Arquivo: src/test/resources/schemas/usuarios/user-by-id-schema.json
givenWithAllure()
    .basePath(ROTA_USUARIOS + "/" + userId)
.when()
    .get()
.then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/usuarios/user-by-id-schema.json"));
```

### ❌ PROIBIDO: Assertions Vagas

```java
// ERRADO: Sem validação clara
.body("response", notNullValue())  // ❌ Muito genérico

// ERRADO: Sem verificação de tipo
.body("id", anything())            // ❌ Não valida
```

---

## 7. Sincronização, Execução Síncrona & Paralelismo

### ❌ PROIBIDO: Esperas Arbitrárias

```java
// ERRADO: Espera cega, degrada performance e mascara problemas
Thread.sleep(2000);                        // ❌ Proibido
```

### ✅ Requisições Síncronas e Execução Paralela JUnit 5

- Requisições HTTP via Rest Assured são **síncronas** e retornam a resposta completa antes de iniciar o bloco `.then()`.
- O projeto utiliza paralelismo no JUnit 5 (`@Execution(ExecutionMode.CONCURRENT)` e `@TestInstance(Lifecycle.PER_CLASS)`).
- **Regra de Ouro para Concorrência**:
  - Nunca altere `RestAssured.basePath` ou `RestAssured.requestSpecification` estaticamente dentro de um `@Test`.
  - Sempre passe `.basePath(...)`, `.headers(...)` e `.body(...)` na instância local retornada por `givenWithAllure()`.

---

## 8. Independência de Testes

### ✅ OBRIGATÓRIO: Cada Teste é Isolado

```java
@Test
void testA_CreateUser() {
    final String emailA = FakerUtils.randomEmail();  // Dados próprios
    // Cria usuário A
}

@Test
void testB_UpdateUser() {
    final String emailB = FakerUtils.randomEmail();  // Dados novos, não reutiliza A
    // Cria e atualiza usuário B
}
```

→ Não dependem um do outro. Podem executar em qualquer ordem.

### ❌ PROIBIDO: Compartilhamento de Estado

```java
// ERRADO: testB depende de testA
private static String sharedUserId;  // ❌

@Test
void testA_CreateUser() {
    sharedUserId = createUser(...).extract().path("id");  // ❌
}

@Test
void testB_UpdateUser() {
    updateUser(sharedUserId, ...);  // ❌ Depende de A ter rodado antes
}
```

---

## 9. Extração & Reutilização de Dados

### ✅ Extrair ID para Ações Subsequentes (Dentro do Mesmo Teste)

```java
@Test
@DisplayName("CT08 - Criar e atualizar usuário no mesmo fluxo isolado")
void createAndUpdateUser() {
    final String email = FakerUtils.randomEmail();
    final String name = FakerUtils.randomName();
    
    // Criar usuário e extrair ID
    final String userId = givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_USUARIOS)
        .body(bodyPayload(name, email, DEFAULT_PASSWORD, "false"))
    .when()
        .post()
    .then()
        .statusCode(201)
    .extract()
        .path(KEY_ID);  // Extrai "_id"
    
    // Usar ID para atualizar (mesmo teste, sequencial e isolado)
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_USUARIOS + "/" + userId)
        .body(bodyPayload(name, FakerUtils.randomEmail(), DEFAULT_PASSWORD, "false"))
    .when()
        .put()
    .then()
        .statusCode(200)
        .body(KEY_MESSAGE, equalTo("Registro alterado com sucesso"));
}
```

---

## 10. Integração com Allure

### ✅ OBRIGATÓRIO: Usar `givenWithAllure()`

Ativa captura automática de requisição/resposta no Allure Report.

```java
givenWithAllure()
    .contentType(ContentType.JSON)
    .body(payload)
.when()
    .post(endpoint)
.then()
    .statusCode(201);
```

### ✅ Anexos de Contexto (Opcional)

```java
@Step("Criar usuário com email {email}")
private void createUserStep(String email) {
    givenWithAllure()
        .contentType(ContentType.JSON)
        .body(Map.of("email", email, "password", "senha123"))
    .when()
        .post(ROTA_USUARIOS)
    .then()
        .statusCode(201);
}
```

---

## 11. Tratamento de Erros & Casos Negativos

### ✅ Validar Códigos de Erro Esperados

```java
@Test
@DisplayName("CT06 - Login com e-mail inexistente retorna 401")
void loginWithNonExistentEmailReturns401() {
    final Map<String, Object> payload = Map.of(
        KEY_EMAIL, FakerUtils.randomEmail(),  // Email que não existe
        KEY_PASSWORD, "senha123"
    );
    
    givenWithAllure()
        .contentType(ContentType.JSON)
        .body(payload)
    .when()
        .post(ROTA_LOGIN)
    .then()
        .statusCode(401)
        .body(KEY_MESSAGE, containsString("não encontrado"));
}
```

### ✅ Validar Mensagens de Erro

```java
.then()
    .statusCode(400)
    .body("message", allOf(
        containsString("inválido"),
        not(containsString("system error"))  // Sem expor stack traces
    ));
```

---

## 12. Aplicação Prática da Heurística VADER nos Testes

Ao criar ou revisar uma suíte de testes, aplique sistematicamente os 5 pilares definidos em `Heuristicas.MD`:

1. **[V] Verbs (Verbos HTTP)**:
   - Valide não apenas o status do `POST`/`PUT`/`DELETE`, mas também o efeito real consultando `GET /recurso/{id}` em seguida.
2. **[A] Authentication & Authorization**:
   - Em endpoints protegidos (`/produtos`, `/carrinhos`), inclua testes para:
     - Usuário sem header `Authorization` → `401 Unauthorized`
     - Usuário com `administrador = "false"` em rota administrativa → `403 Forbidden`
     - Usuário com `administrador = "true"` → `200`/`201`
3. **[D] Data (Dados & Contratos)**:
   - Use `@ParameterizedTest` (`@MethodSource` ou `@CsvFileSource`) para campos obrigatórios e formatos inválidos.
   - Valide o JSON Schema de respostas com `matchesJsonSchemaInClasspath(...)`.
4. **[E] Errors (Erros de Negócio e Integridade)**:
   - Valide bloqueios de integridade referencial (ex: impedir exclusão de usuário com carrinho ativo ou criação de carrinho com estoque insuficiente) conferindo a `KEY_MESSAGE` exata.
5. **[R] Responsiveness (Capacidade de Resposta & Paralelismo)**:
   - Anote suítes funcionais com `@TestInstance(Lifecycle.PER_CLASS)` e `@Execution(ExecutionMode.CONCURRENT)`.
   - Quando aplicável, valide SLA de resposta com `.time(lessThan(3000L))`.

---

## 13. Checklist Pre-Commit

Antes de fazer commit em testes Java:

- [ ] `mvn clean test` passa 100% localmente
- [ ] `mvn pmd:check` sem erros críticos
- [ ] `mvn checkstyle:check` sem violations
- [ ] Sem `System.out.println()` ou `@Disabled` sem justificativa
- [ ] Todos os testes herdam `BaseApiTest`
- [ ] DisplayName e nomeclatura seguem padrão
- [ ] Dados dinâmicos com `FakerUtils`
- [ ] Sem `Thread.sleep()` ou `waitForTimeout()`
- [ ] Cada teste é independente (pode rodar em qualquer ordem)

---

## Referências

- 📖 Rest Assured: https://rest-assured.io/
- 📖 JUnit 5: https://junit.org/junit5/
- 📖 Allure: https://docs.qameta.io/allure/
- 📖 JSON Schema: https://json-schema.org/
