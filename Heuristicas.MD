# 🔍 VADER — Heurísticas para Testes de API (ServeRest)

Heurísticas são guias práticos e mnemônicos que ajudam a explorar e testar APIs de forma sistemática, identificando problemas funcionais, de segurança, de contrato e de resiliência com eficiência.

Neste projeto, a heurística **VADER** (**V**erbs, **A**uthentication, **D**ata, **E**rrors, **R**esponsiveness) atua como o **modelo oficial de design e cobertura de testes**, integrado diretamente à governança de IA em `.kiro/steering/` e às especificações em `.kiro/specs/`.

---

## 🧭 Os 5 Pilares do Checklist VADER

### 1. 🔤 V — Verbs (Verbos HTTP & Semântica REST)
Valide a semântica de cada verbo HTTP (`GET`, `POST`, `PUT`, `DELETE`) para o recurso:
- Códigos de retorno adequados por operação (`200 OK`, `201 Created`).
- **Segurança e Idempotência**: `GET` não altera estado; chamadas repetidas de `PUT` ou `DELETE` mantêm comportamento consistente; `PUT` em ID inexistente realiza *upsert* (`201 Created`) quando previsto pela regra do ServeRest.
- **Consistência de Efeitos Colaterais**: mutações (`POST`, `PUT`, `DELETE`) refletem imediatamente nas consultas subsequentes (`GET`) e no estoque/vínculo entre recursos.

### 2. 🔐 A — Authentication & Authorization (Autenticação e Autorização)
Teste os mecanismos de autenticação (JWT em `/login`) e controle de acesso baseado em perfil (RBAC):
- **Matriz de Perfis**: Anônimo (sem token), Usuário Padrão (`administrador: "false"`) e Administrador (`administrador: "true"`).
- **Validação de Token**: Token válido (`200`/`201`), token ausente, expirado ou malformado (`401 Unauthorized`), e tentativa de acesso a rota exclusiva de administrador por usuário comum (`403 Forbidden`).

### 3. 🗃️ D — Data (Dados, Limites e Contratos)
Analise a integridade, tipagem e formato dos dados de entrada (request) e saída (response):
- **Entrada**: Campos obrigatórios ausentes ou vazios, formatos inválidos (ex: CSV de e-mails inválidos), duplicidade de chaves únicas (e-mail, produto repetido no carrinho) e limites de negócio (estoque insuficiente).
- **Saída & Contrato**: Presença de todos os campos obrigatórios, ausência de atributos não mapeados (`cpf`, `phone`, `error`) e validação estrita de contrato via **JSON Schema** (`matchesJsonSchemaInClasspath`).

### 4. ⚠️ E — Errors (Tratamento de Erros e Mensagens)
Explore o tratamento de erros esperados e a resiliência da API:
- Retornos determinísticos `400 Bad Request`, `401 Unauthorized` e `403 Forbidden` com mensagens claras e em conformidade com o domínio.
- Prevenção de exclusão inconsistente (ex: bloquear `DELETE /usuarios/{id}` quando o usuário possui carrinho ativo, retornando `idCarrinho`).
- Ausência de vazamento de *stack traces* ou erros internos (`500 Internal Server Error`).

### 5. ⚡ R — Responsiveness (Capacidade de Resposta & SLA)
Avalie o desempenho, limites de taxa (*rate limiting*) e comportamento sob estresse:
- **Teste de Carga (NFR)**: Execução automatizada via **JMeter Java DSL** (`RateLimitLoadTests`) simulando 100 requisições/segundo durante 10 segundos para garantir **zero erros `5xx`** e monitorar respostas `429 Too Many Requests`.
- **SLA de Resposta Funcional**: Monitoramento do tempo de resposta das requisições nas suítes funcionais (`.time(lessThan(3000L))`) e suporte à execução paralela (`@Execution(ExecutionMode.CONCURRENT)`).

---

## 📊 Matriz de Rastreabilidade VADER ↔ Cenários Automatizados (31 Cenários)

| Pilar VADER | Login (`LoginFeatureTests`) | Usuários (`UsersFeatureTests`) | Carrinhos (`CartsFeatureTests`) | Carga / NFR (`RateLimitLoadTests`) |
|---|---|---|---|---|
| **1. V — Verbs** | `CT01` (`POST /login`) | `CT01`, `CT02`, `CT13` (`POST`)<br>`CT03`, `CT04`, `CT10` (`GET`)<br>`CT05`, `CT06` (`PUT`)<br>`CT07`, `CT08` (`DELETE`) | `CT01` (`POST`, `GET`, `DELETE /concluir-compra`)<br>`CT02` (`DELETE /cancelar-compra` + devolução de estoque) | `GET /usuarios` sob alta concorrência |
| **2. A — Auth** | `CT01` (Token JWT gerado)<br>`CT02` (`401` credenciais inválidas)<br>`CT04` (`403` usuário padrão em `POST /produtos`) | `CT01` (`administrador: "false"`)<br>`CT02` (`administrador: "true"`) | `CT01`, `CT02` (Header `Authorization` válido)<br>`CT03` (`401` criar carrinho sem token) | — |
| **3. D — Data** | `CT03` (campos vazios)<br>`CT05` (`@ParameterizedTest` CSV de e-mails inválidos) | `CT09`, `CT16` (e-mail duplicado em `POST`/`PUT`)<br>`CT11` (tipagem e não vazio)<br>`CT12` (ausência de campos extras)<br>`CT17` (`@ParameterizedTest` campos obrigatórios)<br>`CT18` (**JSON Schema** `user-by-id-schema.json`) | `CT04` (limite de 1 carrinho por usuário)<br>`CT06` (estoque insuficiente)<br>`CT07` (produto duplicado no mesmo payload) | — |
| **4. E — Errors** | `CT02` (`401`)<br>`CT03`, `CT05` (`400` por campo)<br>`CT04` (`403` rota exclusiva admin) | `CT07`, `CT08`, `CT15` (`400` ID inexistente)<br>`CT09`, `CT16`, `CT17` (`400` validação)<br>`CT14` (`400` bloqueio de exclusão com carrinho associado) | `CT03` (`401` token ausente/inválido)<br>`CT04`, `CT06`, `CT07` (`400` regra de negócio)<br>`CT05` (`400` ID fora do padrão de 16 caracteres alfanuméricos) | Verificação de `status5xxCount == 0` e contabilização de `429` em JTL |
| **5. R — Responsiveness** | Execução paralela (`CONCURRENT`) | Execução paralela (`CONCURRENT`) | Execução paralela (`CONCURRENT`) | `shouldSupport100RequestsPerSecondWithoutServerErrors` (100 req/s por 10s via JMeter DSL) |

---

## 📈 Avaliação Crítica e Plano de Evolução (Backlog de Qualidade)

| Item | O que está coberto | O que falta ou está parcialmente coberto | Ação Recomendada (Kiro Specs) | Avaliação |
|---|---|---|---|---|
| **1. V — Verbs** | CRUD completo de usuários, autenticação, criação/consulta/conclusão/cancelamento de carrinho e criação de produto. | Comportamento de *upsert* no `PUT /usuarios/{id}` (quando ID não existe deve criar com `201`), `GET /carrinhos` (listagem geral) e `GET, PUT, DELETE /produtos`. | Incluir cenários de *upsert* em `SPEC-USERS-01` e criar suíte dedicada em `SPEC-PRODUCTS-01`. | 🟢 **Boa, com evolução planejada para Produtos** |
| **2. A — Auth** | Login válido/inválido, bloqueio `401` sem token em carrinho e bloqueio `403` para usuário não-admin em produtos. | Token malformado, token sem prefixo `Bearer`, token de usuário recém-excluído e tentativa de `DELETE /produtos/{id}` por usuário comum. | Expandir matriz RBAC em `SPEC-AUTH-01` e `SPEC-PRODUCTS-01`. | 🟡 **Parcialmente coberta (foco em expandir RBAC)** |
| **3. D — Data** | Campos obrigatórios parametrizados (`@MethodSource` e `@CsvFileSource`), duplicidade, regras de estoque e JSON Schema em `GET /usuarios/{id}`. | Schemas JSON para `/login`, `/usuarios` (lista), `/carrinhos` e `/produtos`; limites numéricos (`preco <= 0`, `quantidade <= 0`). | Criar schemas em `src/test/resources/schemas/` para todas as rotas principais. | 🟢 **Boa para usuários; em expansão de Schemas** |
| **4. E — Errors** | Erros `400`, `401` e `403` validados com mensagens exatas e integridade referencial (usuário com carrinho, produto sem estoque). | Validação de ID com menos/mais de 16 caracteres alfanuméricos em `/usuarios/{id}` e `/produtos/{id}`; exclusão de produto vinculado a carrinho. | Adicionar testes negativos de integridade referencial em `SPEC-PRODUCTS-01`. | 🟢 **Forte cobertura de erros funcionais** |
| **5. R — Responsiveness** | Teste de carga JMeter DSL (`100 req/s` em `/usuarios`) com geração de `.jtl` e `analysis.log`, além de suítes funcionais paralelas. | Asserção explícita de tempo de resposta (`.time(lessThan(...))`) nos testes funcionais críticos. | Adotar validação de SLA nas rotas críticas via Rest Assured. | 🟢 **Coberta via JMeter DSL + JUnit Paralelo** |

---

## 🔗 Como Usar o VADER com Kiro Steering

1. **Ao criar uma nova Spec (`.kiro/specs/<feature>/requirements.md`)**:
   - Classifique cada requisito `R1..Rn` com a(s) letra(s) correspondente(s) do VADER: `[V]`, `[A]`, `[D]`, `[E]`, `[R]`.
   - Verifique se a feature possui pelo menos 1 requisito para cada dimensão aplicável antes de iniciar a codificação.
2. **Ao implementar testes (`src/test/java/dev/serverest/**/*.java`)**:
   - Siga os padrões de implementação VADER documentados em [`.kiro/steering/api-rest-assured.md`](.kiro/steering/api-rest-assured.md).
3. **Na revisão de PR / Definition of Done**:
   - Valide a cobertura da feature contra o checklist em [`.kiro/steering/qa-global-dod.md`](.kiro/steering/qa-global-dod.md).
