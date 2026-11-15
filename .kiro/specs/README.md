# 📋 Specs Directory - Especificações de Features

Este diretório contém a especificação completa de cada feature, mapeando requisitos de negócio para tarefas de implementação.

## 🗂️ Estrutura

Cada feature tem um diretório com dois arquivos:

```
specs/
├── authentication/
│   ├── requirements.md     # Requisitos EARS (comportamento esperado)
│   └── tasks.md            # Tarefas práticas (o que fazer)
├── users-management/
│   ├── requirements.md
│   └── tasks.md
├── cart-checkout/
│   ├── requirements.md
│   └── tasks.md
└── products/
    ├── requirements.md
    └── tasks.md
```

---

## 📝 Template: Como Criar uma Nova Spec

### Passo 1: Criar Diretório

```bash
mkdir .kiro/specs/nova-feature
```

### Passo 2: Criar `requirements.md`

Estrutura básica:

```markdown
# Requisitos: [Feature Name] [SPEC-XX-YY]

## Objetivo
Descrever em 1-2 linhas o que esta feature faz.

## Requisitos de Comportamento (EARS Format + Heurística VADER)

Classifique cada requisito com o pilar correspondente de `Heuristicas.MD` (`[V] Verbs`, `[A] Auth`, `[D] Data`, `[E] Errors`, `[R] Responsiveness`):

### R1 [V]: [Descrição do Requisito - Caminho Feliz / Verbo HTTP]

**Quando** [condição inicial]
**Então** o sistema deve:
- Validação 1
- Validação 2
- ...

### R2 [A/D/E]: [Próximo Requisito - Autenticação, Dados ou Erros]
...

## Critérios de Aceite (Checklist)

- [ ] Teste CTxx: [Descrição]
- [ ] Teste CTyp: [Descrição]
...

## Prioridade

🔴 CRÍTICA / 🟠 ALTA / 🟡 MÉDIA / 🔵 BAIXA

## Dependências

- Qual feature precisa estar pronta?
- Qual endpoint precisa estar disponível?
```

### Passo 3: Criar `tasks.md`

Estrutura básica:

```markdown
# Tarefas de Implementação - [Feature Name] [SPEC-XX-YY]

## Fase 1: Preparação

### Task 1.1: [Descrição]
- [ ] Sub-task 1
- [ ] Sub-task 2

## Fase 2: Automação

### Task 2.1: Implementar suíte de testes

**Arquivo:** `src/test/java/dev/serverest/...`

- [ ] Classe criada
- [ ] Cada teste com @DisplayName

### Task 2.2: Teste CTxx - [Descrição]

**Requisito:** [Link para requirements.md#rX]

```java
// Exemplo de implementação
```

## Fase 3: Qualidade

### Task 3.1: Validar DoD

- [ ] mvn clean test passa
- [ ] mvn pmd:check passa
- [ ] mvn checkstyle:check passa

## Checklist Final

- [ ] Todos os requisitos têm testes
- [ ] Documentação atualizada
- [ ] Pronto para merge

| Task | Prioridade | Estimativa | Status |
|------|----------|-----------|--------|
| 1.x | 🔴 | 30 min | ⏳ |
| 2.x | 🔴 | 120 min | ⏳ |
| 3.x | 🟠 | 30 min | ⏳ |

**Total:** ~3 horas
```

---

## 🔍 Exemplo Real: Feature Users Management

### 1️⃣ Entender o Endpoint

Consulte a [documentação do ServeRest](https://serverest.dev/) ou `TESTING_API.MD`:

**Endpoint:** `GET, POST, PUT, DELETE /usuarios`

**Operações:**
- `POST /usuarios` — Criar novo usuário
- `GET /usuarios` — Listar usuários (sem filtro)
- `GET /usuarios/{id}` — Consultar usuário por ID
- `PUT /usuarios/{id}` — Atualizar usuário
- `DELETE /usuarios/{id}` — Deletar usuário

### 2️⃣ Mapear Requisitos (EARS)

**R1: Criar usuário com dados válidos**
- Quando: POST `/usuarios` com email + senha válidos
- Então: Retorna 201, usuário criado, ID gerado

**R2: Email duplicado deve retornar 409**
- Quando: POST `/usuarios` com email já existente
- Então: Retorna 409 Conflict, mensagem clara

**R3: Listar usuários retorna array**
- Quando: GET `/usuarios`
- Então: Retorna 200, array de usuários com schema

**R4: Consultar usuário por ID**
- Quando: GET `/usuarios/{id}` com ID válido
- Então: Retorna 200, dados do usuário

**R5: Atualizar usuário**
- Quando: PUT `/usuarios/{id}` com novos dados
- Então: Retorna 200, usuário atualizado

**R6: Deletar usuário**
- Quando: DELETE `/usuarios/{id}`
- Então: Retorna 200, usuário deletado

### 3️⃣ Criar requirements.md

```markdown
---
inclusion: manual
---

# Requisitos: Gestão de Usuários [SPEC-USERS-01]

## Objetivo

Garantir que os endpoints de gestão de usuários (`GET, POST, PUT, DELETE /usuarios`) funcionem conforme especificado.

## Requisitos de Comportamento

### R1: Criar Usuário com Dados Válidos

**Quando** o usuário enviar POST para `/usuarios` com email, senha, nome válidos

**Então** o sistema deve:
- Retornar **201 Created**
- Incluir o `_id` do novo usuário
- Usuário poder fazer login imediatamente
- Email registrado não pode ser duplicado

...
```

### 4️⃣ Criar tasks.md

```markdown
---
inclusion: manual
---

# Tarefas - Gestão de Usuários [SPEC-USERS-01]

## Fase 2: Automação

### Task 2.1: Implementar UsersFeatureTests

**Arquivo:** `src/test/java/dev/serverest/usuarios/UsersFeatureTests.java`

...

### Task 2.2: CT10 - Criar usuário com dados válidos

```java
@Test
@DisplayName("CT10 - Criar usuário com dados válidos retorna 201")
void createUserWithValidDataReturns201() {
    final String email = FakerUtils.randomEmail();
    final String name = FakerUtils.randomName();
    
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_USUARIOS)
        .body(bodyPayload(name, email, DEFAULT_PASSWORD, "false"))
    .when()
        .post()
    .then()
        .statusCode(201)
        .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
        .body(KEY_ID, notNullValue());
}
```

...
```

---

## 🎯 Fluxo Completo de uma Nova Feature

1. **Analisar Endpoint** → Ler doc ou Swagger
2. **Mapear Requisitos** → EARS format em `requirements.md`
3. **Listar Tarefas** → Checklist prático em `tasks.md`
4. **Implementar Testes** → Java + Rest Assured
5. **Validar DoD** → mvn clean test + pmd + checkstyle
6. **Documentar** → TESTING_API.MD + README
7. **Revisar & Merge** → GitHub PR

---

## 📊 Features Planejadas

| Feature | Status | Endpoint | Testes Estimados |
|---------|--------|----------|-----------------|
| **Authentication** | 🟢 Pronto | `POST /login` | 6 (CT01-CT06) |
| **Users Management** | 🟡 Planejando | `GET,POST,PUT,DELETE /usuarios` | 12-15 |
| **Cart & Checkout** | 🟡 Planejando | `GET,POST,DELETE /carrinhos`, `/cancelar` | 8-10 |
| **Products** | 🟡 Planejando | `POST /produtos` | 5-7 |
| **Performance** | 🟡 Planejando | Rate limiting (NFR) | 1-2 |

**Total Estimado:** 40-45 testes

---

## 💡 Dicas Importantes

### ✅ Ao Criar requirements.md

- Use EARS format: **Quando**...** Então**...
- Seja específico: status codes, nomes de campos, messages
- Inclua casos negativos: erro 400, 401, 409, 500
- Mapeie cada requisito para 1 teste mínimo (CT01, CT02)

### ✅ Ao Criar tasks.md

- Quebre tarefas em partes menores (30-60 min cada)
- Inclua snippets de código como exemplo
- Liste validações explícitas (status, body, schema)
- Estime tempo realista (QA + Kiro + revisão)

### ❌ Evite

- Requisitos vagos ("deve funcionar")
- Testes sem DisplayName
- Dados hardcoded
- Esperas arbitrárias
- Estado compartilhado entre testes

---

## 🔗 Referências

- 📖 [DoD Global](../steering/qa-global-dod.md)
- 📖 [Diretrizes de Teste](../steering/api-rest-assured.md)
- 📖 [EARS Format](https://alistair.cockburn.us/ears/) (referência externa)
- 📖 [ServeRest API Docs](https://serverest.dev)
- 📖 [Mapeamento de Testes](../../TESTING_API.MD)

---

Pronto para criar uma nova spec? Comece duplicando a pasta `authentication/` e adapte para sua feature!
