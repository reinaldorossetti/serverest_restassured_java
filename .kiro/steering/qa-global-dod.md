---
inclusion: always
---

# Definition of Done (DoD) - Qualidade Global

## Referências de Governança e Regras Estáticas
- #[[file:pmd-ruleset.xml]]
- #[[file:checkstyle-suppressions.xml]]
- #[[file:Heuristicas.MD]]
- #[[file:TESTING_API.MD]]

## Checklist de Qualidade para Qualquer Tarefa/Feature

Antes de um trabalho ser considerado **PRONTO**, valide todos os itens abaixo:

### 1. Especificação & Documentação (Alinhado ao VADER)

- [ ] Requisitos mapeados em `.kiro/specs/<feature>/requirements.md` (formato EARS)
- [ ] Cada requisito classificado com sua dimensão **VADER** (`[V] Verbs`, `[A] Auth`, `[D] Data`, `[E] Errors`, `[R] Responsiveness`) conforme `Heuristicas.MD`
- [ ] Critérios de aceite claramente definidos cobrindo Caminho Feliz (`[V]`) e Cenários Negativos/Limites (`[A]`, `[D]`, `[E]`)
- [ ] Dependências de recursos (autenticação, massa de dados isolada) documentadas

### 2. Automação de Testes & Cobertura VADER

- [ ] Todos os cenários de aceite têm cobertura de teste automatizado
- [ ] Cobertura das dimensões VADER na suíte:
  - **[V] Verbs**: Status code correto (`200`, `201`) e persistência validada via `GET` quando aplicável
  - **[A] Auth**: Validação de token ausente/inválido (`401`) e permissão de perfil admin vs. padrão (`403`) em rotas protegidas
  - **[D] Data**: Dados dinâmicos via `FakerUtils`, validação de campos obrigatórios/formatos e contrato via `matchesJsonSchemaInClasspath`
  - **[E] Errors**: Validação explícita das mensagens de erro (`400`, `401`, `403`) e regras de integridade referencial
  - **[R] Responsiveness**: Testes seguros para execução paralela (`@Execution(ExecutionMode.CONCURRENT)`) e sem `Thread.sleep()`
- [ ] Testes estão em suíte apropriada (`LoginFeatureTests`, `UsersFeatureTests`, `CartsFeatureTests`, `RateLimitLoadTests`)
- [ ] Cada teste é **independente** (sem compartilhamento de estado)

### 3. Qualidade do Código

- [ ] Código segue padrões em `structure.md` (nomeclatura, imports, organização)
- [ ] Não há código duplicado (princípio DRY)
- [ ] Métodos de teste descritivos com `@DisplayName` e lógica clara
- [ ] Sem linhas de código "comentada" ou debug (`System.out.println()`)
- [ ] Sem esperas arbitrárias (`Thread.sleep()`) ou `page.waitForTimeout()`

### 4. Análise Estática & Linting

- [ ] `mvn validate pmd:check` passa sem erros
- [ ] `mvn checkstyle:check` passa (avisos permitidos, conforme `checkstyle-suppressions.xml`)
- [ ] Sem warnings críticos de cobertura ou complexidade ciclomática

### 5. CI/CD & Relatórios

- [ ] Testes executam com sucesso em `mvn clean test`
- [ ] Allure Report mostra cobertura e status 🟢 (100% passou)
- [ ] Logs de execução claros e sem warnings não tratados
- [ ] GitHub Actions pipeline passa em ambos runners (Ubuntu + Windows)

### 6. Compatibilidade & Ambientes

- [ ] Testes rodam contra `https://serverest.dev` (produção)
- [ ] Testes rodam contra `http://localhost:3000` (Docker local, quando CI=true)
- [ ] Sem dependências de dados hardcoded por ambiente
- [ ] Suporta execução paralela sem race conditions

### 7. Performance & Escalabilidade

- [ ] Tempo total de suíte não ultrapassa threshold definido
- [ ] Sem testes excessivamente lentos (> 10s cada)
- [ ] Testes parametrizados não geram explosão combinatorial
- [ ] Payloads e responses validados, não há overhead de parsing

### 8. Segurança & Privacidade

- [ ] Nenhuma credencial (senha, token) hardcoded em código
- [ ] `.env` contém apenas URLs; secrets gerenciados via GitHub Secrets (CI)
- [ ] Logs não expõem dados sensíveis (tokens, senhas)
- [ ] Respeito a política de dados e compliance (LGPD/GDPR se aplicável)

### 9. Documentação & Rastreabilidade

- [ ] Cenário de teste mapeado com identificador (ex: CT01, CT02)
- [ ] Requisito de negócio linkado (ex: [SPEC-AUTH-01])
- [ ] `tasks.md` em `.kiro/specs/<feature>/` atualizado e marcado como ✅
- [ ] Readme atualizado se houver mudança estrutural

### 10. Revisão & Aprovação

- [ ] Código revisado por pelo menos 1 par (code review)
- [ ] Feedback abordado e comentários resolvidos
- [ ] Branch atualizado com `main` antes de merge
- [ ] Nenhum conflito em merge pendente

---

## Exemplos de Passagem & Reprovação

### ✅ PASSOU

```java
@Test
@DisplayName("CT12 - Atualizar usuário com e-mail válido deve retornar 200")
void updateUserWithValidEmailReturns200() {
    final String initialEmail = FakerUtils.randomEmail();
    final String newEmail = FakerUtils.randomEmail();
    final String userName = FakerUtils.randomName();
    final String userId = createUserAndReturnId(initialEmail, DEFAULT_PASSWORD);
    
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_USUARIOS + "/" + userId)
        .body(bodyPayload(userName, newEmail, DEFAULT_PASSWORD, "false"))
    .when()
        .put()
    .then()
        .statusCode(200)
        .body(KEY_MESSAGE, equalTo("Registro alterado com sucesso"));
}
```

**Validação DoD:**
- ✅ Teste independente (cria próprio usuário)
- ✅ Dados dinâmicos (`FakerUtils.randomEmail()`)
- ✅ DisplayName descritivo + mapeado (CT12)
- ✅ Validação clara (status 200 + mensagem)
- ✅ Sem state sharing, sem hardcode

### ❌ REPROVADO

```java
@Test
void updateUser() {  // ❌ Nome genérico, sem DisplayName
    String userId = "12345";  // ❌ ID hardcoded
    Thread.sleep(2000);       // ❌ Espera arbitrária
    
    given().body("{\"email\":\"user@test.com\"}").when().put("/usuarios/12345")
        .then().statusCode(200);
}
```

**Problemas:**
- ❌ Sem DisplayName descritivo
- ❌ ID hardcoded, sem dados dinâmicos
- ❌ `Thread.sleep()` proibido
- ❌ Payload em String, sem tipagem
- ❌ Não herda `BaseApiTest`

---

## Métricas de Qualidade Alvo

| Métrica | Alvo | Frequência de Validação |
|---------|------|----------------------|
| **Taxa de Sucesso de Testes** | 100% | Cada push (CI) |
| **Cobertura de Cenários** | 90%+ do mapa VADER | Sprint review |
| **PMD Issues Críticas** | 0 | Cada commit |
| **Complexidade Ciclomática Média** | ≤ 10 | Mensal |
| **Tempo de Execução Suite** | ≤ 5 min | Baseline, por runner |
| **Taxa de Flaky Tests** | < 1% | Mensal |

---

## Processo de Escalonamento (se DoD não é atingido)

1. **Não está pronto?** → Marque como "Não Pronto" no board
2. **Identifique o bloqueador**: Qual item do DoD não foi atingido?
3. **Comunique**: Abra issue ou avise o time
4. **Remedie**: Aplique a correção necessária (novo teste, refactor, doc)
5. **Re-valide**: Verifique todos os itens novamente
6. **Marque como Pronto** quando 100% dos items estão ✅

---

## Referências

- 📖 **Heurística VADER**: [Heuristicas.MD](../../../Heuristicas.MD)
- 📖 **Especificações de Requisitos**: [TESTING_API.MD](../../../TESTING_API.MD)
- 📖 **Padrões de Teste**: [structure.md](./structure.md)
- 📖 **Stack Técnica**: [tech.md](./tech.md)
