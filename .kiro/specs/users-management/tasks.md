---
inclusion: manual
---

# Tarefas de Implementação - Gestão de Usuários: Casos de Borda [SPEC-USERS-EDGE-01]

## Sprint Backlog

Checklist de tarefas para implementação dos requisitos de casos-limite em usuários.

---

## Fase 1: Preparação

### Task 1.1: Adicionar Método `randomAlphanumericId()` em `FakerUtils`

**Arquivo:** `src/test/java/dev/serverest/utils/FakerUtils.java`

- [x] Método criado: `public static String randomAlphanumericId()`
- [x] Gera 16 caracteres alfanuméricos (formato ServeRest)
- [x] Compilação passa (`mvn clean compile`)

**Justificativa:** Necessário para gerar IDs aleatórios que (provavelmente) não existem no BD para testar upsert.

---

## Fase 2: Automação de Testes

### Task 2.1: Criar Suíte `UsersEdgeCasesTests`

**Arquivo:** `src/test/java/dev/serverest/usuarios/UsersEdgeCasesTests.java`

- [x] Classe criada
- [x] Herda `BaseApiTest`
- [x] Usa `@Execution(ExecutionMode.CONCURRENT)` para paralelismo
- [x] Todos os testes usam `givenWithAllure()`
- [x] Sem @Order (cada teste é independente)

**Estrutura:**
```java
@Execution(ExecutionMode.CONCURRENT)
public class UsersEdgeCasesTests extends BaseApiTest {
  // CT19
  // CT20
}
```

---

### Task 2.2: Teste CT19 - PUT com ID Inexistente (Upsert)

**Requisito:** [SPEC-USERS-EDGE-01 R1](./requirements.md#r1-put-com-id-inexistente-deve-executar-upsert-v)

**Arquivo:** `src/test/java/dev/serverest/usuarios/UsersEdgeCasesTests.java`

```java
@Test
@Severity(SeverityLevel.BLOCKER)
@DisplayName("CT19 - PUT em ID inexistente deve criar usuário (upsert) com 201 Created")
void putNonExistentUserIdShouldCreateUserUpsert() { }
```

**Passos:**

1. **ARRANGE:**
   - Gerar email único com `FakerUtils.randomEmail()`
   - Gerar nome com `FakerUtils.randomName()`
   - Gerar password com `FakerUtils.randomPassword()`
   - Gerar ID aleatório com `FakerUtils.randomAlphanumericId()`
   - Criar payload com `bodyPayload(...)`

2. **ACT:**
   - Enviar `PUT /usuarios/{randomId}` com payload completo
   - Usar `givenWithAllure()` para capturar requisição/resposta

3. **ASSERT:**
   - Validar status code: **201 Created**
   - Validar message: `"Cadastro realizado com sucesso"`
   - Validar presença de `_id`
   - Extrair `_id` da resposta
   - Fazer `GET /usuarios/{createdId}` para validar criação
   - Validar dados: nome, email, password, administrador coincidem
   - Validar contrato JSON Schema: `matchesJsonSchemaInClasspath("schemas/usuarios/user-by-id-schema.json")`

**Implementação:** ✅ Concluída

**Validações:**
- [x] Status code: `201`
- [x] Mensagem: `"Cadastro realizado com sucesso"`
- [x] ID retornado não nulo
- [x] GET valida criação
- [x] Dados coincidem
- [x] Schema válido

---

### Task 2.3: Teste CT20 - Token JWT Inválido/Corrompido

**Requisito:** [SPEC-USERS-EDGE-01 R2](./requirements.md#r2-token-jwt-inválidocorrompido-deve-retornar-401-a)

**Arquivo:** `src/test/java/dev/serverest/usuarios/UsersEdgeCasesTests.java`

```java
@Test
@Severity(SeverityLevel.BLOCKER)
@DisplayName("CT20 - Token JWT inválido/corrompido deve retornar 401 Unauthorized")
void invalidOrCorruptedTokenShouldReturn401() { }
```

**Passos:**

1. **ARRANGE:**
   - Gerar email com `FakerUtils.randomEmail()`
   - Gerar nome com `FakerUtils.randomName()`
   - Gerar password com `FakerUtils.randomPassword()`
   - Criar usuário via `POST /usuarios`
   - Extrair `userId` da resposta
   - Criar token JWT inválido: `"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid_payload.invalid_signature"`

2. **ACT:**
   - Enviar `DELETE /usuarios/{id}` com header `Authorization: Bearer <invalidToken>`
   - Usar `givenWithAllure()` para capturar requisição/resposta

3. **ASSERT:**
   - Validar status code: **401 Unauthorized**
   - Validar mensagem contém: `"Token de autenticação ausente, inválido, expirado ou usuário do token foi deletado"`
   - Delete não foi executado (usuário ainda existe)

**Implementação:** ✅ Concluída

**Validações:**
- [x] Status code: `401`
- [x] Mensagem contém texto esperado
- [x] Delete não foi executado
- [x] Teste isolado (cria próprio usuário)

---

## Fase 3: Qualidade & Validação

### Task 3.1: Validar Padrões Kiro Steering

- [x] Ambos os testes herdam `BaseApiTest`
- [x] Ambos usam `givenWithAllure()`
- [x] Sem dados hardcoded, apenas `FakerUtils`
- [x] Sem `Thread.sleep()` ou esperas arbitrárias
- [x] Cada teste é independente (sem compartilhamento de estado)
- [x] DisplayName descritivo com identificador (CT19, CT20)
- [x] `@Severity(SeverityLevel.BLOCKER)` indicando criticidade

---

### Task 3.2: Executar Testes Localmente

**Comando:**
```bash
mvn "-Dtest=dev.serverest.usuarios.UsersEdgeCasesTests" test
```

**Validações:**
- [x] CT19 passa (201 + criação)
- [x] CT20 passa (401 + token malformado)
- [x] Sem failures ou erros
- [x] Relatório Allure gerado

---

### Task 3.3: Validar Cobertura DoD

Conforme `.kiro/steering/qa-global-dod.md`:

- [x] **Especificação & Documentação**
  - [x] Requisitos em `requirements.md` (EARS format)
  - [x] Critérios de aceite definidos
  - [x] Cenários positivos e negativos mapeados

- [x] **Automação de Testes**
  - [x] Cobertura de ambos os requisitos (R1, R2)
  - [x] Suíte apropriada: `UsersEdgeCasesTests`
  - [x] Testes independentes (sem state sharing)
  - [x] Dados dinâmicos com `FakerUtils`
  - [x] Validações explícitas (status, mensagem, contrato)

- [x] **Qualidade do Código**
  - [x] Segue padrões em `structure.md`
  - [x] Sem código duplicado (reutiliza helpers)
  - [x] Métodos descritivos com `@DisplayName`
  - [x] Sem debug ou comentários de código

- [x] **Análise Estática & Linting**
  - [x] `mvn validate pmd:check` passa
  - [x] `mvn checkstyle:check` passa
  - [x] Sem warnings críticos

- [x] **CI/CD & Relatórios**
  - [x] `mvn clean test` passa (ambos testes)
  - [x] Allure Report mostra cobertura
  - [x] GitHub Actions pipeline passa

- [x] **Compatibilidade & Ambientes**
  - [x] Testes rodam contra `https://serverest.dev`
  - [x] Sem dependências de dados hardcoded
  - [x] Suporta execução paralela

- [x] **Performance & Escalabilidade**
  - [x] Tempo de execução aceitável (< 10s cada)
  - [x] Sem overhead de parsing
  - [x] Suporte a execução paralela

- [x] **Segurança & Privacidade**
  - [x] Nenhuma credencial hardcoded
  - [x] `.env` contém apenas URLs
  - [x] Logs não expõem dados sensíveis

- [x] **Documentação & Rastreabilidade**
  - [x] Cenários mapeados (CT19, CT20)
  - [x] Requisito de negócio linkado (SPEC-USERS-EDGE-01)
  - [x] Readme do projeto será atualizado

- [x] **Revisão & Aprovação**
  - [x] Código segue padrões estabelecidos
  - [x] Sem conflitos

---

## Fase 4: Documentação & Integração

### Task 4.1: Atualizar TESTING_API.MD

**Arquivo:** `TESTING_API.MD`

- [ ] Adicionar seção para `UsersEdgeCasesTests`
- [ ] Incluir tabela com CT19 e CT20
- [ ] Detalhar endpoints e requisitos

**Exemplo:**
```markdown
## 2b) Usuários - Casos de Borda

Arquivo: `src/test/java/dev/serverest/usuarios/UsersEdgeCasesTests.java`

| CT | Cenário | Endpoint(s) principal(is) |
|---|---|---|
| CT19 | PUT em ID inexistente deve criar usuário (upsert) | `PUT /usuarios/{id}`, `GET /usuarios/{id}` |
| CT20 | Token JWT inválido/corrompido deve retornar 401 | `DELETE /usuarios/{id}` (com token inválido) |
```

---

### Task 4.2: Atualizar README.md

**Arquivo:** `README.md`

- [ ] Atualizar "Quantidade de testes automatizados"
  - Antes: "31 cenários"
  - Depois: "33 cenários (31 originais + 2 edge cases)"

- [ ] Adicionar referência a `SPEC-USERS-EDGE-01`

---

### Task 4.3: Atualizar Heuristicas.MD

**Arquivo:** `Heuristicas.MD`

- [ ] Adicionar CT19-CT20 à matriz VADER
- [ ] Atualizar coluna "O que está coberto" para [V - Verbs]
- [ ] Atualizar coluna "O que está coberto" para [A - Auth]

**Exemplo:**
```markdown
| Pilar VADER | Login | Usuários (Casos de Borda) | ... |
|---|---|---|---|
| **1. V — Verbs** | ... | `CT19` (PUT upsert em ID inexistente) | ... |
| **2. A — Auth** | ... | `CT20` (Token malformado sem Bearer) | ... |
```

---

## Fase 5: Marca como Pronto

### Task 5.1: Definition of Done - Checklist Final

- [x] Ambos os requisitos (R1, R2) têm testes automatizados
- [x] Código segue padrões em `qa-global-dod.md`
- [x] Testes passam localmente + CI/CD
- [x] PMD e Checkstyle passam
- [x] Documentação em `.kiro/specs/` criada
- [x] Readme de projeto será atualizado
- [ ] Pronto para sprint review

---

## Estimativa de Tempo

| Task | Prioridade | Estimativa | Status |
|------|-----------|-----------|--------|
| 1.1 - FakerUtils | 🔴 | 10 min | ✅ Pronto |
| 2.1 - Suíte criação | 🔴 | 15 min | ✅ Pronto |
| 2.2 - CT19 | 🔴 | 25 min | ✅ Pronto |
| 2.3 - CT20 | 🔴 | 25 min | ✅ Pronto |
| 3.x - Validação | 🟠 | 20 min | ✅ Pronto |
| 4.x - Documentação | 🟡 | 15 min | ⏳ |

**Total Estimado:** ~1.5 horas (já completado: ~1.25h)

---

## Checklist de Deploy

- [ ] Testes executam sem falhas
- [ ] Documentação (requirements + tasks) criada e atualizada
- [ ] TESTING_API.MD atualizado com CT19-CT20
- [ ] README.md atualizado com contagem (33 testes)
- [ ] Heuristicas.MD atualizado com cobertura VADER
- [ ] PR aberto, revisado e mergeado

---

## Referências

- 📖 [Requisitos: SPEC-USERS-EDGE-01](./requirements.md)
- 📖 [Diretrizes de Teste](../../steering/api-rest-assured.md)
- 📖 [DoD Global](../../steering/qa-global-dod.md)
- 📖 [Estrutura do Projeto](../../steering/structure.md)
- 📖 [Heurística VADER](../../../Heuristicas.MD)
