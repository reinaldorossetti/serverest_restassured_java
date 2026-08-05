# 📋 Matriz de Rastreabilidade de Requisitos vs. Cobertura de Testes

---

## 1. 🌐 Contexto do Projeto

Aplicação de gerenciamento de usuários e fluxos de e-commerce que expõe uma API RESTful.
- **Base URL sugerida**: [https://serverest.dev](https://serverest.dev)
- **Tecnologias**: Java 23, Rest Assured 6.0.0, JUnit 5, Allure Report, GitHub Actions CI/CD.

---

## 2. 🎯 Requisitos do Desafio e Status de Cobertura

| ID | Requisito | Detalhes do Requisito | Status | Classe / Teste de Cobertura |
| :---: | :--- | :--- | :---: | :--- |
| **REQ-01** | Autenticação via Token JWT | Autenticação realizada via endpoint `/login` com retorno e uso do cabeçalho `Authorization` nas rotas protegidas. | ✅ **Coberto** | `LoginFeatureTests.java`<br>• `loginWithValidCredentials()`<br>• `loginAndUseTokenInProtectedRoute()` |
| **REQ-02** | Limitação de Taxa (*Rate Limit*) | API deve suportar até 100 requisições por minuto sem quebras ou bloqueios indevidos. | ✅ **Coberto** | `BaseApiTest.java` + Execução em Paralelo JUnit 5 (`ExecutionMode.CONCURRENT`) e chamadas contínuas via RestAssured. |
| **REQ-03** | Endpoints de Usuários (CRUD completo) | Implementação e validação de todas as rotas CRUD de `/usuarios`: | ✅ **Coberto** | `UsersFeatureTests.java` |
| | ↳ `GET /usuarios` | Retornar lista de todos os usuários com paginação e schema válido. | ✅ **Coberto** | • `listAllUsersAndValidateStructure()` |
| | ↳ `POST /usuarios` | Criar novo usuário com validação de payload obrigatório. | ✅ **Coberto** | • `createNormalUser()`<br>• `createAdminUser()` |
| | ↳ `GET /usuarios/{id}` | Retornar os detalhes de um usuário específico por ID. | ✅ **Coberto** | • `getUserById()` |
| | ↳ `PUT /usuarios/{id}` | Atualizar informações de um usuário existente. | ✅ **Coberto** | • `updateAdminUser()` |
| | ↳ `DELETE /usuarios/{id}` | Excluir um usuário do sistema. | ✅ **Coberto** | • `UsersFeatureTests.java` |
| **REQ-04** | Campos Obrigatórios no Payload de Usuário | Validação dos campos obrigatórios no `POST /usuarios`: `nome` (string), `email` (string), `password` (string), `administrador` (string). | ✅ **Coberto** | `UsersFeatureTests.java`<br>• `createNormalUser()`<br>• `createAdminUser()` |
| **REQ-05** | Suíte de Testes Automatizados | Desenvolver automação cobrindo os cenários positivos e negativos da API. | ✅ **Coberto** | `LoginFeatureTests.java`, `UsersFeatureTests.java`, `CartsFeatureTests.java` |
| **REQ-06** | Pipeline de CI/CD | Integrar a execução dos testes em esteira automatizada (GitHub Actions). | ✅ **Coberto** | `.github/workflows/rest-assured-api-pipeline.yml` |
| **REQ-07** | Geração e Envio de Relatórios | Disponibilizar relatórios Allure (HTML no GitHub Pages e PDF anexado por e-mail). | ✅ **Coberto** | `allure-maven`, `allure-pdf`, `peaceiris/actions-gh-pages`, `dawidd6/action-send-mail` |
| **REQ-08** | Documentação do Projeto | Fornecer instruções de execução e arquitetura no repositório GitHub. | ✅ **Coberto** | `README.md`, `TESTING_API.MD`, `Requisitos.md` |

---

## 3. 🧪 Resumo da Cobertura de Testes

- **Total de Requisitos Mapeados**: 8/8
- **Índice de Cobertura**: 💯% **100% Coberto**
- **Relatórios Gerados**:
  - 📊 **Allure Report (HTML)**: Publicado via GitHub Pages.
  - 📄 **Allure Report (PDF)**: Gerado e enviado automaticamente por e-mail no encerramento da pipeline.