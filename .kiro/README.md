# 📋 Kiro Steering & Specs - ServerRest API Testing

Bem-vindo ao repositório de **governança e especificações** do projeto ServeRest API Testing. Este diretório (`.kiro/`) contém toda a estrutura de diretrizes, padrões e especificações que guiam a automação de testes.

## 🗂️ Estrutura

### 📌 `/steering/` — Diretrizes ("Como Fazer")

Arquivos de governança que definem padrões globais de qualidade, arquitetura e boas práticas.

| Arquivo | Inclusão | Propósito |
|---------|----------|----------|
| **`product.md`** | `always` | Propósito do produto, personas, objetivos de negócio |
| **`tech.md`** | `always` | Stack tecnológica, libs homologadas, restrições |
| **`structure.md`** | `always` | Organização de pastas, convenções, padrões |
| **`qa-global-dod.md`** | `always` | Definition of Done — checklist de qualidade global |
| **`api-rest-assured.md`** | `fileMatch` | Diretrizes de automação com Rest Assured + Java (`fileMatchPattern`: `src/test/java/dev/serverest/**/*.java`) |

**Tipo de Inclusão (`https://kiro.dev/docs/steering/`):**
- `always`: Incluído **automaticamente** em todas as interações com o Kiro (padrão para Foundational Steering Files)
- `fileMatch`: Ativado **dinamicamente** quando arquivos abertos atendem ao padrão em `fileMatchPattern`
- `manual`: Carregado **sob demanda** quando referenciado explicitamente via `#steering-file-name`
- `auto`: Ativado **semanticamente** conforme `name` e `description` no YAML frontmatter

---

### 🎯 `/specs/` — Especificações ("O Que Fazer")

Estrutura de features com requisitos e tarefas de implementação.

#### `authentication/`

Especificação completa de autenticação no ServeRest.

- **`requirements.md`** — Requisitos de comportamento (EARS format) para o endpoint `/login`
  - R1: Login com credenciais válidas
  - R2: Login com credenciais inválidas
  - R3: Validação de formato de email
  - R4: Campos obrigatórios
  - R5: Payload vazio/malformado
  - R6: Token JWT em autenticação subsequente

- **`tasks.md`** — Checklist de implementação
  - CT01-CT06: Mapeamento de testes
  - CSV com dados de teste
  - Validações de qualidade (PMD, Checkstyle)
  - Status de conclusão

#### `(futuro) users-management/`
Gestão de usuários: CRUD em `/usuarios`

#### `(futuro) cart-checkout/`
Fluxo de carrinho e checkout: `/carrinhos`, `/carrinhos/cancelar-compra`

#### `(futuro) products/`
Gestão de produtos: `POST /produtos`

---

## 🚀 Como Usar

### 1. Leia a Governança Primeiro

Ao iniciar trabalho no projeto, consulte os arquivos de `steering/`:

1. **`product.md`** — Entender o propósito e criticidade
2. **`tech.md`** — Validar que está usando a stack homologada
3. **`structure.md`** — Organizar código conforme padrão
4. **`qa-global-dod.md`** — Validar DoD antes de marcar como pronto
5. **`api-rest-assured.md`** — Ao escrever testes, seguir diretrizes

### 2. Implemente Conforme a Especificação

Ao trabalhar em uma feature, use os arquivos de `specs/`:

1. Abra `specs/<feature>/requirements.md`
2. Entenda os requisitos (EARS format)
3. Abra `specs/<feature>/tasks.md`
4. Siga o checklist de tarefas
5. Marque como concluído (✅) conforme avança

### 3. Valide Contra DoD

Antes de fazer commit ou PR:

1. Abra `qa-global-dod.md`
2. Execute checklist:
   - [ ] Especificação & Documentação
   - [ ] Automação de Testes
   - [ ] Qualidade do Código
   - [ ] Análise Estática (PMD, Checkstyle)
   - [ ] CI/CD & Relatórios
   - [ ] Compatibilidade & Ambientes
   - [ ] Performance & Escalabilidade
   - [ ] Segurança & Privacidade
   - [ ] Documentação & Rastreabilidade
   - [ ] Revisão & Aprovação

---

## 📊 Status Atual

### Steering ✅

- [x] `product.md` — Governança de produto
- [x] `tech.md` — Stack tecnológica
- [x] `structure.md` — Organização do código
- [x] `qa-global-dod.md` — Definition of Done
- [x] `api-rest-assured.md` — Diretrizes de automação

### Specs 🔄

| Feature | Requirements | Tasks | Status |
|---------|--------------|-------|--------|
| **Authentication** | ✅ | ✅ | 🟢 Pronto para implementação |
| Users Management | ⏳ | ⏳ | 🟡 Em planejamento |
| Cart & Checkout | ⏳ | ⏳ | 🟡 Em planejamento |
| Products | ⏳ | ⏳ | 🟡 Em planejamento |

---

## 🔗 Links Relacionados

### Documentação Principal do Projeto

- 📖 **README.md** — Visão geral, pré-requisitos, instalação
- 📖 **TESTING_API.MD** — Mapeamento de testes vs endpoints
- 📖 **SWAGGER_TESTS.md** — Catálogo de testes do Swagger
- 📖 **Heuristicas.MD** — Análise VADER de cobertura
- 📖 **Requisitos.md** — Especificações de negócio

### Referências Externas

- 🌐 **Artigo Kiro Steering** — [O Guia Definitivo de Kiro Steering para QA e Testes](https://www.linkedin.com/pulse/o-guia-definitivo-de-kiro-steering-para-qa-e-testes-mateus-rossetti-5e2jf/)
- 📚 **Rest Assured Docs** — https://rest-assured.io/
- 📚 **JUnit 5 Docs** — https://junit.org/junit5/
- 📚 **Allure Docs** — https://docs.qameta.io/allure/
- 📚 **ServeRest API** — https://serverest.dev

---

## 💡 Boas Práticas

### ✅ Ao Escrever um Teste

1. Estender `BaseApiTest` (não teste sozinho)
2. Usar `givenWithAllure()` para capturar requisição/resposta
3. Gerar dados com `FakerUtils` (dinâmicos, não hardcoded)
4. Usar `@DisplayName` descritivo com identificador (CT01, CT02)
5. Validar status code + resposta estruturada
6. Manter testes **isolados** (sem compartilhamento de estado)

### ✅ Ao Criar uma Nova Feature

1. Abrir issue em `.kiro/specs/<feature>/requirements.md`
2. Documentar requisitos em EARS format
3. Listar tarefas concretas em `tasks.md`
4. Implementar conforme checklist
5. Validar contra DoD antes de marcar como pronto

### ❌ O Que Evitar

- ❌ Hardcoding de dados (use `FakerUtils`)
- ❌ `Thread.sleep()` ou esperas arbitrárias (use validações web-first)
- ❌ Código duplicado (refatore para helpers)
- ❌ Testes com dependência entre eles (cada um independente)
- ❌ Sem `@DisplayName` ou nomes genéricos (CT01, CT02, descritivos)
- ❌ Ignorar avisos PMD/Checkstyle

---

## 📞 Suporte & Contribuição

Se tiver dúvidas sobre o steering ou specs:

1. Consulte o arquivo correspondente em `steering/` ou `specs/`
2. Verifique o artigo original: [O Guia Definitivo de Kiro Steering](https://www.linkedin.com/pulse/o-guia-definitivo-de-kiro-steering-para-qa-e-testes-mateus-rossetti-5e2jf/)
3. Abra issue no repositório

---

## 🎯 Próximos Passos

1. **Implementar CT01-CT06** conforme `.kiro/specs/authentication/tasks.md`
2. **Criar specs** para `users-management/`, `cart-checkout/`, `products/`
3. **Integrar Kiro steering** com CI/CD do GitHub Actions
4. **Publicar relatório** no GitHub Pages com cobertura de specs

---

**Última atualização:** Outubro 2026  
**Versão Steering:** 1.0  
**Status:** 🟢 Ativo e em uso
