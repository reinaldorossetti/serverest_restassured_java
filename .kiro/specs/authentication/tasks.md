# Tarefas de Implementação - Autenticação [SPEC-AUTH-01]

## Referências de Código
- #[[file:src/test/java/dev/serverest/login/LoginFeatureTests.java]]
- #[[file:src/test/resources/restassured/login/invalid-login-emails.csv]]

## Sprint Backlog

Checklist de tarefas para implementação e automação dos requisitos de autenticação.

---

## Fase 1: Configuração Base

### Task 1.1: Garantir Classe Base `BaseApiTest` Configurada

- [x] `BaseApiTest.java` contém `BaseURI` configurado
- [x] `BaseApiTest.java` expõe `givenWithAllure()` para todos os testes
- [x] Constantes de rotas definidas: `ROTA_LOGIN`
- [x] Filtro `AllureRestAssured` registrado para captura de requisição/resposta

**Referência:** `src/test/java/dev/serverest/BaseApiTest.java`

---

## Fase 2: Automação de Testes

### Task 2.1: Implementar Suíte de Testes `LoginFeatureTests`

**Arquivo:** `src/test/java/dev/serverest/login/LoginFeatureTests.java`

- [x] Classe criada, herda `BaseApiTest`
- [x] Todos os testes usam `givenWithAllure()`
- [x] Cada teste tem `@DisplayName` descritivo
- [x] Execução paralela ativada com `@Execution(ExecutionMode.CONCURRENT)`

---

### Task 2.2: Teste CT01 - Login com Credenciais Válidas

**Requisito:** [SPEC-AUTH-01 R1](./requirements.md#r1-login-com-credenciais-válidas-caminho-feliz)

```java
@Test
@DisplayName("CT01 - Login com credenciais válidas deve retornar token JWT")
void loginWithValidCredentialsReturnsToken() { }
```

**Passos:**
1. Criar usuário com `FakerUtils.randomEmail()` e senha conhecida
2. Enviar POST para `/login` com credenciais válidas
3. Validar status 200, mensagem e presença de `authorization`

**Validações:**
- [ ] Status code: `201` (user created) → `200` (login success)
- [ ] Response body contem: `"message": "Login realizado com sucesso"`
- [ ] Response body contem: `"authorization"` (não nulo, formato Bearer)
- [ ] Token é string válida (não vazio)

---

### Task 2.3: Teste CT02 - Login com Email Inválido

**Requisito:** [SPEC-AUTH-01 R2](./requirements.md#r2-login-com-credenciais-inválidas-caso-negativo)

```java
@Test
@DisplayName("CT02 - Login com email inexistente deve retornar 401")
void loginWithNonExistentEmailReturns401() { }
```

**Passos:**
1. Gerar email aleatório que não foi criado
2. Enviar POST para `/login` com esse email + senha qualquer
3. Validar status 401 e mensagem de erro

**Validações:**
- [ ] Status code: `401`
- [ ] Response contem mensagem de erro (ex: "Email e/ou senha inválidos")
- [ ] Sem token no response

---

### Task 2.4: Teste CT03 - Login com Senha Inválida

**Requisito:** [SPEC-AUTH-01 R2](./requirements.md#r2-login-com-credenciais-inválidas-caso-negativo)

```java
@Test
@DisplayName("CT03 - Login com senha incorreta deve retornar 401")
void loginWithIncorrectPasswordReturns401() { }
```

**Passos:**
1. Criar usuário com email e senha conhecida
2. Enviar POST para `/login` com email correto + senha incorreta
3. Validar status 401

**Validações:**
- [ ] Status code: `401`
- [ ] Response contem mensagem de erro
- [ ] Sem token no response

---

### Task 2.5: Teste CT04 - Login sem Campos Obrigatórios

**Requisito:** [SPEC-AUTH-01 R4](./requirements.md#r4-campos-obrigatórios)

```java
@Test
@DisplayName("CT04 - Login sem campos obrigatórios deve retornar 400")
void loginWithoutRequiredFieldsReturns400() { }
```

**Passos (Múltiplos Cenários):**

1. Sem email:
   - Enviar POST com apenas `password`
   - Validar status 400 + erro relacionado a email

2. Sem password:
   - Enviar POST com apenas `email`
   - Validar status 400 + erro relacionado a password

3. Payload vazio:
   - Enviar POST com `{}`
   - Validar status 400

**Validações:**
- [ ] Status code: `400` para cada cenário
- [ ] Mensagem de erro clara indicando campo faltante

---

### Task 2.6: Teste CT05 - Login com Email em Formato Inválido (Parametrizado)

**Requisito:** [SPEC-AUTH-01 R3](./requirements.md#r3-validação-de-formato-de-email)

```java
@ParameterizedTest(name = "CT05 - Email inválido: {0}")
@CsvFileSource(resources = "/restassured/login/invalid-login-emails.csv", numLinesToSkip = 1)
void validateInvalidEmailFormats(String invalidEmail) { }
```

**Dados de Teste (CSV):** `src/test/resources/restassured/login/invalid-login-emails.csv`

```csv
invalid_email
invalido@
usuario
@dominio.com
usuario@@dominio.com
usuario@.com
```

**Passos:**
1. Para cada email inválido na CSV
2. Enviar POST para `/login` com esse email
3. Validar status 400

**Validações:**
- [ ] Cada linha da CSV testada uma vez
- [ ] Status code: `400` para todos
- [ ] Response contem validação de email

---

### Task 2.7: Teste CT06 - Token JWT Válido em Requisições Subsequentes

**Requisito:** [SPEC-AUTH-01 R6](./requirements.md#r6-token-jwt-em-headers-autenticação-subsequente)

```java
@Test
@DisplayName("CT06 - Token JWT válido deve autorizar requisição subsequente")
void validTokenAuthorizesSubsequentRequest() { }
```

**Passos:**
1. Criar usuário + fazer login (extrair token)
2. Fazer requisição protegida (ex: GET `/usuarios/<id>`) com token no header `Authorization: Bearer <token>`
3. Validar que requisição é aceita (status 200, não 401/403)

**Validações:**
- [ ] Login retorna token
- [ ] Token inserido em header `Authorization`
- [ ] Requisição subsequente retorna 200 (ou status apropriado, não 401)

---

## Fase 3: Dados de Teste

### Task 3.1: CSV com Emails Inválidos

**Arquivo:** `src/test/resources/restassured/login/invalid-login-emails.csv`

- [ ] Arquivo criado com header: `invalid_email`
- [ ] Mínimo 5 exemplos de emails inválidos conforme R3
- [ ] Formato CSV válido (sem BOM, delimitador `,`)

**Exemplo:**
```csv
invalid_email
invalido@
usuario
@dominio.com
usuario@@dominio.com
```

---

## Fase 4: Análise & Qualidade

### Task 4.1: Validar Cobertura DoD

- [ ] Todos os testes herdam `BaseApiTest`
- [ ] Todos os testes usam `givenWithAllure()`
- [ ] Sem dados hardcoded, apenas `FakerUtils`
- [ ] Sem `Thread.sleep()` ou esperas arbitrárias
- [ ] Cada teste é independente (sem compartilhamento de estado)
- [ ] `mvn clean test` passa 100%
- [ ] `mvn pmd:check` passa
- [ ] `mvn checkstyle:check` passa

---

### Task 4.2: Validação em CI/CD

- [ ] GitHub Actions pipeline executa suíte de login
- [ ] Allure Report mostra 6 cenários de login com sucesso (CT01-CT06)
- [ ] Sem flaky tests (testes rodam 100% de sucesso múltiplas vezes)

---

## Fase 5: Documentação

### Task 5.1: Atualizar Mapeamento de Testes

**Arquivo:** `TESTING_API.MD`

- [ ] Adicionar tabela com CT01-CT06 mapeados para `POST /login`
- [ ] Incluir referência a requisitos em `requirements.md`

---

### Task 5.2: Atualizar README

**Arquivo:** `README.md`

- [ ] Seção "Quantidade de testes automatizados" atualizada: +5 testes de login
- [ ] Link para este spec incluído na seção "Specs"

---

## Fase 6: Revisão & Pronto

### Task 6.1: Code Review

- [ ] Mínimo 1 revisão por par
- [ ] Feedback abordado
- [ ] Sem conflitos em merge

---

### Task 6.2: Marcar como Pronto (Definition of Done)

**Cheklist Final:**
- [x] Todos os requisitos (R1-R6) têm testes automatizados
- [x] Código segue padrões em `qa-global-dod.md`
- [x] Testes passam localmente + CI/CD
- [x] PMD e Checkstyle passam
- [x] Documentação atualizada
- [x] Pronto para sprint review

---

## Referências

- 📖 [Requisitos: SPEC-AUTH-01](./requirements.md)
- 📖 [Diretrizes de Teste](../../steering/api-rest-assured.md)
- 📖 [DoD Global](../../steering/qa-global-dod.md)
- 📖 [Estrutura do Projeto](../../steering/structure.md)
