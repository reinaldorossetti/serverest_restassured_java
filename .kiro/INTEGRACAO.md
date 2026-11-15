# 🔗 Integração - Kiro Steering com Projeto

Guia de como integrar Kiro Steering com a documentação e workflow existente do projeto.

## 📝 Próximos Passos para o README.md

Adicionar seção no `README.md` raiz do projeto apontando para Kiro Steering:

```markdown
## 📋 Governança & Specs (Kiro Steering)

Este projeto utiliza **Kiro Steering** para governança de testes e especificações.

### Documentação de Governance

Toda a diretrização e especificações estão em `.kiro/`:

- **Iniciantes:** Comece em [`.kiro/QUICK_START.md`](.kiro/QUICK_START.md) (10 min)
- **Visão Completa:** [`.kiro/README.md`](.kiro/README.md)
- **Estrutura Visual:** [`.kiro/steering/structure.md`](.kiro/steering/structure.md)

### Diretrizes de Desenvolvimento

| Aspecto | Arquivo | Link |
|---------|---------|------|
| **Propósito** | `.kiro/steering/product.md` | Entender objetivos |
| **Stack Técnica** | `.kiro/steering/tech.md` | Java, Maven, libs |
| **Estrutura & Padrões** | `.kiro/steering/structure.md` | Onde colocar código |
| **DoD (Definition of Done)** | `.kiro/steering/qa-global-dod.md` | Checklist de qualidade |
| **Como Escrever Testes** | `.kiro/steering/api-rest-assured.md` | Padrões de teste |

### Especificações de Features

Cada feature tem seus requisitos + tarefas mapeadas:

| Feature | Requisitos | Tarefas | Status |
|---------|-----------|--------|--------|
| **Authentication** | [requirements.md](.kiro/specs/authentication/requirements.md) | [tasks.md](.kiro/specs/authentication/tasks.md) | 🟢 Pronto |
| **Users Management** | (planejando) | (planejando) | 🟡 Próxima |
| **Cart & Checkout** | (planejando) | (planejando) | 🟡 Próxima |
| **Products** | (planejando) | (planejando) | 🟡 Próxima |

### Fluxo de Trabalho

1. Abra a spec: `.kiro/specs/<feature>/requirements.md`
2. Entenda os requisitos (EARS format)
3. Implemente tarefas: `.kiro/specs/<feature>/tasks.md`
4. Valide contra DoD: `.kiro/steering/qa-global-dod.md`
5. Commit e push

### Exemplo: Autenticação

Para implementar testes de autenticação (CT01-CT06):

```bash
# 1. Ler requisitos
cat .kiro/specs/authentication/requirements.md

# 2. Ler tarefas (checklist)
cat .kiro/specs/authentication/tasks.md

# 3. Implementar LoginFeatureTests.java
# (Seguir padrões em .kiro/steering/api-rest-assured.md)

# 4. Validar qualidade
mvn clean test
mvn validate pmd:check checkstyle:check

# 5. Commit
git checkout -b feat/ct01-ct06-login
git add src/test/java/dev/serverest/login/LoginFeatureTests.java
git commit -m "feat: CT01-CT06 - Autenticação conforme SPEC-AUTH-01"
git push -u origin feat/ct01-ct06-login
```

### Referência Rápida de Comandos

```bash
# Executar testes de login
mvn "-Dtest=dev.serverest.login.LoginFeatureTests" test

# Validar código + análise estática
mvn validate pmd:check checkstyle:check

# Gerar relatório Allure
mvn allure:serve
```

---

## 🔄 Atualizar TESTING_API.MD

Adicionar seção de rastreabilidade de specs:

```markdown
## 🔗 Mapeamento Specs-Testes

Cada teste automatizado está vinculado a um requisito de spec:

| Spec | Requisito | Teste | Arquivo |
|------|-----------|-------|---------|
| [SPEC-AUTH-01](.kiro/specs/authentication/requirements.md) | R1 | CT01 | LoginFeatureTests.java |
| SPEC-AUTH-01 | R2 | CT02 | LoginFeatureTests.java |
| SPEC-AUTH-01 | R3 | CT05 | LoginFeatureTests.java |
| ... | ... | ... | ... |
```

---

## 📊 Atualizar Heuristicas.MD

Adicionar rastreabilidade VADER → Specs:

```markdown
## Cobertura VADER por Spec

### VERBS (Verbos HTTP)

- **POST /login** → [SPEC-AUTH-01](.kiro/specs/authentication/requirements.md#r1) (R1)
- **GET /usuarios** → SPEC-USERS-01 (futuro)
- **POST /usuarios** → SPEC-USERS-01 (futuro)
- ...

### AUTHENTICATION

- **Login com token JWT** → [SPEC-AUTH-01](.kiro/specs/authentication/requirements.md#r6) (R6)
- **Autorização em endpoints protegidos** → SPEC-USERS-01 (futuro)
- ...
```

---

## 🚀 Atualizar GitHub Actions Workflow

Adicionar step para validar specs:

```yaml
# .github/workflows/rest-assured-api-pipeline.yml

- name: Validate Specs vs Tests Coverage
  run: |
    # Script para verificar se cada spec tem testes
    echo "✅ Validando cobertura de specs..."
    # (Implementar verificação de CT01-CT06 em LoginFeatureTests)
    mvn "-Dtest=dev.serverest.login.LoginFeatureTests" test
```

Opcional: Publicar relatório de specs no GitHub Pages juntamente com Allure Report.

---

## 📈 Criar Dashboard de Specs (Futuro)

Gerar página em GitHub Pages com:

```html
<h2>Status de Implementação</h2>
<table>
  <tr>
    <th>Spec</th>
    <th>Requisitos</th>
    <th>Testes</th>
    <th>Taxa de Sucesso</th>
    <th>Status</th>
  </tr>
  <tr>
    <td>SPEC-AUTH-01</td>
    <td>6</td>
    <td>6 (CT01-CT06)</td>
    <td>100%</td>
    <td>✅ Pronto</td>
  </tr>
  <tr>
    <td>SPEC-USERS-01</td>
    <td>8</td>
    <td>0</td>
    <td>0%</td>
    <td>🟡 Planejando</td>
  </tr>
</table>
```

---

## 🎓 Documentação para Onboarding

Adicionar na wiki do GitHub:

### "Começando com Kiro Steering"

1. Clonar repositório
2. Abrir [`.kiro/QUICK_START.md`](.kiro/QUICK_START.md)
3. Ler os 5 pilares em `.kiro/steering/`
4. Escolher feature em `.kiro/specs/`
5. Implementar conforme tarefas

---

## ✅ Checklist de Integração

- [ ] Atualizar README.md com seção "Governança & Specs"
- [ ] Atualizar TESTING_API.MD com mapeamento Specs-Testes
- [ ] Atualizar Heuristicas.MD com rastreabilidade VADER
- [ ] Adicionar step de validação no GitHub Actions (opcional)
- [ ] Comunicar ao time via issue/discussão
- [ ] Criar exemplar de implementação (CT01-CT06 Authentication)

---

## 📞 Comunicação ao Time

**Issue/Discussão no GitHub:**

```markdown
## 🎯 Governança Kiro Steering Ativa!

Implementamos Kiro Steering para governança de testes e especificações.

### O que muda?

✅ **Antes:** Testes sem requisitos explícitos, DoD variável
✅ **Depois:** Requisitos em EARS → Testes mapeados → DoD checklist

### Como usar?

1. Iniciantes: [`.kiro/QUICK_START.md`](.kiro/QUICK_START.md) (10 min)
2. Detalhes: [`.kiro/README.md`](.kiro/README.md)
3. Estrutura: [`.kiro/ESTRUTURA.txt`](.kiro/ESTRUTURA.txt)

### Próximas Features

🟢 **Authentication** (SPEC-AUTH-01) — Pronto para implementação (CT01-CT06)
🟡 **Users Management** — Em planejamento
🟡 **Cart & Checkout** — Em planejamento
🟡 **Products** — Em planejamento

### Como Contribuir?

1. Leia o spec em `.kiro/specs/<feature>/`
2. Implemente tarefas em `tasks.md`
3. Valide contra `.kiro/steering/qa-global-dod.md`
4. Commit e PR

Dúvidas? Comente abaixo!
```

---

## 🎉 Próximas Fases

### Fase 1: Implementação (Sprint Atual)
- [ ] CT01-CT06 Authentication
- [ ] Teste em local + CI/CD
- [ ] Merge para `main`

### Fase 2: Expansão (Próximo Sprint)
- [ ] SPEC-USERS-01 (12-15 testes)
- [ ] SPEC-CART-01 (8-10 testes)
- [ ] SPEC-PRODUCTS-01 (5-7 testes)

### Fase 3: Observabilidade (Sprint 3)
- [ ] Dashboard de Specs em GitHub Pages
- [ ] Relatório de cobertura VADER
- [ ] Integração com Allure Report

---

## 📖 Referência

- 📄 Artigo Original: [O Guia Definitivo de Kiro Steering](https://www.linkedin.com/pulse/o-guia-definitivo-de-kiro-steering-para-qa-e-testes-mateus-rossetti-5e2jf/)
- 📄 Documentação Kiro: [Kiro.dev](https://kiro.dev)
- 📄 EARS Format: [Alistair Cockburn](https://alistair.cockburn.us/ears/)

---

**Próximo passo:** Implementar CT01-CT06 conforme `.kiro/specs/authentication/tasks.md`

Bom trabalho! 🎯
