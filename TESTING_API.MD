# Mapeamento de Testes da API ServeRest (estado atual)

Este documento reflete **o estado real do projeto atual** (JUnit 5 + Rest Assured), incluindo testes funcionais e não funcionais.

- Base URL pública: `https://serverest.dev`
- Classe base: `src/test/java/restassured_serverest/BaseApiTest.java`
- Frameworks: JUnit 5, Rest Assured, Hamcrest, Allure

---

## Resumo executivo

### Quantidade de cenários

- `LoginFeatureTests`: **5**
- `UsersFeatureTests`: **18**
- `CartsFeatureTests`: **7**
- `RateLimitLoadTests` (NFR/carga): **1**

**Total: 31 cenários automatizados**

### Endpoints cobertos

- `POST /login`
- `GET, POST, PUT, DELETE /usuarios`
- `GET, POST /carrinhos`
- `DELETE /carrinhos/concluir-compra`
- `DELETE /carrinhos/cancelar-compra`
- `POST /produtos`

> Observação: `POST /produtos` é exercitado em cenários de autorização/autenticação e apoio a fluxos de carrinho.

---

## Mapeamento por suíte

## 1) Login

Arquivo: `src/test/java/restassured_serverest/login/LoginFeatureTests.java`

| CT | Cenário | Endpoint(s) principal(is) |
|---|---|---|
| CT01 | Login com credenciais válidas e token | `POST /usuarios`, `POST /login` |
| CT02 | Login com credenciais inválidas | `POST /login` |
| CT03 | Campos obrigatórios no login | `POST /login` |
| CT04 | Login + tentativa de acesso a rota exclusiva de admin | `POST /usuarios`, `POST /login`, `POST /produtos` |
| CT05 | Formato inválido de e-mail no login | `POST /login` |

---

## 2) Usuários

Arquivo: `src/test/java/restassured_serverest/usuarios/UsersFeatureTests.java`

| CT | Cenário | Endpoint(s) principal(is) |
|---|---|---|
| CT01 | Criação de usuário padrão | `POST /usuarios`, `GET /usuarios/{id}` |
| CT02 | Criação de usuário administrador | `POST /usuarios`, `GET /usuarios/{id}` |
| CT03 | Listagem e estrutura de usuários | `GET /usuarios` |
| CT04 | Busca usuário por ID | `GET /usuarios`, `GET /usuarios/{id}` |
| CT05 | Atualização de usuário admin | `POST /usuarios`, `PUT /usuarios/{id}`, `GET /usuarios/{id}` |
| CT06 | Atualização de usuário padrão | `POST /usuarios`, `PUT /usuarios/{id}`, `GET /usuarios/{id}` |
| CT07 | Exclusão de usuário admin | `POST /usuarios`, `DELETE /usuarios/{id}`, `GET /usuarios/{id}` |
| CT08 | Exclusão de usuário padrão | `POST /usuarios`, `DELETE /usuarios/{id}`, `GET /usuarios/{id}` |
| CT09 | Bloqueio de e-mail duplicado | `POST /usuarios` |
| CT10 | Validações avançadas com filtros | `GET /usuarios` |
| CT11 | Validações condicionais | `GET /usuarios` |
| CT12 | Ausência de campos não mapeados | `GET /usuarios` |
| CT13 | Preparação de dados para validação de cadastro | `POST /usuarios` |
| CT14 | Impedir exclusão de usuário com carrinho associado | `POST /usuarios`, `POST /login`, `POST /produtos`, `DELETE /carrinhos/cancelar-compra`, `POST /carrinhos`, `DELETE /usuarios/{id}` |
| CT15 | Buscar usuário com ID inválido | `GET /usuarios/{id}` |
| CT16 | Impedir atualização com e-mail duplicado | `POST /usuarios`, `PUT /usuarios/{id}` |
| CT17 | Campos obrigatórios no cadastro (parametrizado) | `POST /usuarios` |
| CT18 | Validação de contrato JSON Schema do usuário por ID | `POST /usuarios`, `GET /usuarios/{id}` |

### Contrato (Schema)

- Arquivo de schema: `src/test/resources/schemas/usuarios/user-by-id-schema.json`
- Matcher usado: `matchesJsonSchemaInClasspath(...)`

---

## 3) Carrinhos

Arquivo: `src/test/java/restassured_serverest/carrinhos/CartsFeatureTests.java`

| CT | Cenário | Endpoint(s) principal(is) |
|---|---|---|
| CT01 | Ciclo completo do carrinho (criar, buscar, concluir compra) | `POST /usuarios`, `POST /login`, `DELETE /carrinhos/cancelar-compra`, `POST /produtos`, `POST /carrinhos`, `GET /carrinhos/{id}`, `DELETE /carrinhos/concluir-compra` |
| CT02 | Cancelar compra e devolver estoque | `POST /usuarios`, `POST /login`, `DELETE /carrinhos/cancelar-compra`, `POST /produtos`, `POST /carrinhos`, `DELETE /carrinhos/cancelar-compra` |
| CT03 | Bloquear criação de carrinho sem token | `POST /carrinhos` |
| CT04 | Impedir mais de um carrinho por usuário | `POST /usuarios`, `POST /login`, `DELETE /carrinhos/cancelar-compra`, `POST /produtos`, `POST /carrinhos` |
| CT05 | Carrinho não encontrado por ID inválido | `GET /carrinhos/{id}` |
| CT06 | Impedir carrinho com estoque insuficiente | `POST /usuarios`, `POST /login`, `DELETE /carrinhos/cancelar-compra`, `POST /produtos`, `POST /carrinhos` |
| CT07 | Impedir carrinho com produto duplicado | `POST /usuarios`, `POST /login`, `DELETE /carrinhos/cancelar-compra`, `POST /produtos`, `POST /carrinhos` |

---

## 4) Carga / NFR

Arquivo: `src/test/java/restassured_serverest/performance/RateLimitLoadTests.java`

| Cenário | Objetivo | Configuração atual |
|---|---|---|
| `shouldSupport100RequestsPerSecondWithoutServerErrors` | Simular carga em `/usuarios` e garantir ausência de erro 5xx | `throughputTimer(100)` por `Duration.ofSeconds(10)` |

Detalhes:

- Cenário marcado com `@Disabled` por padrão (execução manual).
- Critério atual: **falha apenas se houver HTTP 5xx**.
- `429` pode ocorrer ou não dependendo da política do ambiente.
- Logs gerados:
  - JTL: `target/jmeter-jtls/rate-limit/*.jtl`
  - Resumo: `target/jmeter-jtls/rate-limit/analysis.log`

---

## Convenções adotadas

- Identificação de cenário via `@DisplayName("CTxx - ...")`.
- Execução paralela nas suítes funcionais (`ExecutionMode.CONCURRENT`).
- Execução em thread única para carga (`ExecutionMode.SAME_THREAD`).
- Payloads preferencialmente tipados com `Map`/`List`.

---

## Observações de manutenção

Se houver inclusão, remoção ou renomeação de cenários, este arquivo deve ser atualizado em conjunto com:

- `README.md` (resumo e contagem)
- classes de teste (`login`, `usuarios`, `carrinhos`, `performance`)
- artefatos de contrato (`schemas/**`) quando aplicável
