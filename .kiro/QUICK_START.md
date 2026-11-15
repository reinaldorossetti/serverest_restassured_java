# ⚡ Quick Start - Kiro Steering

Bem-vindo ao projeto! Se está começando agora, comece por aqui.

## 🎯 1. Entenda o Propósito (2 min)

Leia a primeira seção de `steering/product.md`:

> Repositório de **automação de testes de API REST** para validar a plataforma **ServeRest** usando Rest Assured 6.1.0 e Java 23.

**Por que isso importa?** Testes precisam ser resilientes, independentes e documentados.

---

## 🛠️ 2. Valide o Ambiente (5 min)

Verifique se tem tudo instalado:

```bash
# Java 23+
java -version

# Maven 3.6+
mvn -v

# (Opcional) Docker
docker --version
```

Se algum comando falhar, leia `steering/tech.md` para instruções.

---

## 📚 3. Leia as Três Leis da Governança (10 min)

Abra os três pilares de `steering/`:

1. **`structure.md`** — Onde colocar código? Como nomear?
2. **`api-rest-assured.md`** — Como escrever testes?
3. **`qa-global-dod.md`** — Quando é considerado pronto?

**TL;DR:**
- Todos os testes herdam `BaseApiTest`
- Dados dinâmicos com `FakerUtils`
- Sem `Thread.sleep()`, sem hardcoding, sem estado compartilhado

---

## 🎯 4. Escolha uma Feature para Trabalhar

Abra `.kiro/specs/` e escolha uma feature:

### ✅ Se quer começar agora: **Authentication (Pronto)**

```
.kiro/specs/authentication/
├── requirements.md    ← Leia aqui (requisitos EARS)
└── tasks.md          ← Faça as tarefas (CT01-CT06)
```

**Próximo passo:** [Pular para Task 2.1](#-implemente-a-feature)

### 🔄 Se quer planejar: **Users Management, Cart, Products**

Criaremos specs assim que você tiver tempo.

---

## 🚀 5. Implemente a Feature

### Exemplo: Autenticação (CT01)

**Arquivo:** `src/test/java/dev/serverest/login/LoginFeatureTests.java`

```java
package dev.serverest.login;

import dev.serverest.BaseApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Map;

/** Testes de autenticação na rota /login. */
public class LoginFeatureTests extends BaseApiTest {

    /** Valida login com credenciais válidas e geração de token JWT. */
    @Test
    @DisplayName("CT01 - Login com credenciais válidas retorna token")
    void loginWithValidCredentialsReturnsToken() {
        final String email = FakerUtils.randomEmail();
        final String password = DEFAULT_PASSWORD;

        createUser(email, password, false)
            .then()
            .statusCode(201);

        givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_LOGIN)
            .body(Map.of(KEY_EMAIL, email, KEY_PASSWORD, password))
        .when()
            .post()
        .then()
            .statusCode(200)
            .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
            .body("authorization", notNullValue());
    }
}
```

**Checklist antes de commit:**

- [ ] Hereditário: `extends BaseApiTest`
- [ ] Dados dinâmicos: `FakerUtils.randomEmail()`
- [ ] Sem estado: `FakerUtils` + cria próprio usuário
- [ ] DisplayName: `@DisplayName("CT01 - ...")`
- [ ] Validações: status + resposta estruturada
- [ ] Sem `Thread.sleep()` ou delays arbitrários

---

## ✅ 6. Valide Contra DoD

Antes de fazer commit:

```bash
# Testes passam?
mvn clean test

# Código está limpo?
mvn validate pmd:check checkstyle:check

# Allure Report funciona?
mvn allure:serve
```

Se tudo passou ✅, abra `steering/qa-global-dod.md` e marque os itens como concluído.

---

## 📤 7. Commit & Push

```bash
git checkout -b feat/ct01-login
git add src/test/java/dev/serverest/login/LoginFeatureTests.java
git commit -m "feat: CT01 - Login com credenciais válidas"
git push -u origin feat/ct01-login
```

Abra Pull Request. No GitHub Actions, pipeline roda automaticamente e publica Allure Report.

---

## 🎓 Próximos Passos

### Dia 1-2: Implementar CT01-CT06 (Autenticação)
- [ ] Ler `specs/authentication/requirements.md`
- [ ] Fazer tarefas em `specs/authentication/tasks.md`
- [ ] Validar contra DoD

### Dia 3-4: Criar Specs para Users Management
- [ ] Duplicar `specs/authentication/` → `specs/users-management/`
- [ ] Ler `TESTING_API.MD` para requisitos de `/usuarios`
- [ ] Mapear requisitos em EARS format

### Dia 5: Code Review & Merge
- [ ] Review de pares
- [ ] Feedback abordado
- [ ] Merge para `main`

---

## 🆘 Dúvidas Frequentes

### "Onde coloco testes de API vs E2E?"

**Resposta:** Este projeto é **API-only** (Rest Assured). Se no futuro integrarmos UI (Playwright, Selenium), criar novo diretório `tests/e2e/` com seu próprio steering.

### "Posso usar XPath ou seletores CSS?"

**Resposta:** Este é um projeto de **API**, não UI. XPath/CSS não fazem sentido. Validamos JSON, status HTTP, tokens JWT.

### "Preciso fazer login antes de cada teste?"

**Resposta:** Sim, se o teste requer autenticação. Use `createUser()` + `loginAndExtractToken()` **dentro do teste** (não em `@BeforeEach` global, pois causa state sharing).

### "Posso colocar dados em arquivo CSV?"

**Resposta:** Sim! Para dados parametrizados (ex: emails inválidos). Veja `src/test/resources/restassured/login/invalid-login-emails.csv` como exemplo.

### "E se falhar o teste localmente mas passar no CI?"

**Resposta:** Provável race condition ou timeout. Abra issue, não ignore. Consulte `api-rest-assured.md` seção "Sincronização & Esperas".

---

## 📖 Referências Rápidas

| Dúvida | Arquivo |
|--------|---------|
| Qual stack usar? | `steering/tech.md` |
| Como estruturar pasta? | `steering/structure.md` |
| Como escrever teste? | `steering/api-rest-assured.md` |
| Quando é pronto? | `steering/qa-global-dod.md` |
| Qual spec fazer agora? | `specs/authentication/` |

---

## 🚦 Status do Projeto

```
✅ Steering       Completo e ativo
🟢 Authentication Pronto para implementação
🟡 Users Mgmt     Em planejamento
🟡 Cart & Co      Em planejamento
🟡 Products       Em planejamento
```

**Próxima meta:** 40 testes automatizados (atualmente 31 funcionais + 1 NFR).

---

Bom trabalho! Se tiver dúvidas, consulte o `.kiro/README.md` ou abra issue no repositório.

🎯 **Vamos construir testes de qualidade!**
