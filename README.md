# serverest_restassured_java
Projeto de Automação de Testes usando o RestAssured e Java 23 com ALLURE e GITHUB ACTIONS.

# 🧪 Rest Assured API Testing - ServeRest

[![RestAssured](https://img.shields.io/badge/RestAssured-5.3.2-blue.svg)](https://rest-assured.io/)
[![JUnit5](https://img.shields.io/badge/JUnit-5.10.1-green.svg)](https://junit.org/junit5/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)

Projeto de automação de testes de API utilizando **Rest Assured** para testar a API REST **ServeRest** - uma API gratuita que simula uma loja virtual.

Projeto estruturado com boas práticas, exemplos de testes, validações avançadas e integração com JUnit 5 para execução e relatórios.

URI do repositório: [https://github.com/reinaldorossetti/karate_api_java](https://github.com/reinaldorossetti/karate_api_java)

Reporte na esteira: [https://reinaldorossetti.github.io/karate_api_java/allure-reports/index.html](https://reinaldorossetti.github.io/karate_api_java/allure-reports/index.html)

Testes realizados na API: [TESTING_API.MD](TESTING_API.MD)

---

## 📚 Índice

- [Sobre o Rest Assured](#-sobre-o-rest-assured)
- [Sobre a API ServeRest](#-sobre-a-api-serverest)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Executando os Testes](#-executando-os-testes)
- [Análise Estática de Código (PMD & Checkstyle)](#-análise-estática-de-código-pmd--checkstyle)
- [Exemplos de Testes](#-exemplos-de-testes)
- [Funcionalidades do Rest Assured](#-funcionalidades-do-rest-assured)
- [Relatórios](#-relatórios)
- [Boas Práticas](#-boas-práticas)
- [Recursos Adicionais](#-recursos-adicionais)

---

## 🧪 Sobre o Rest Assured

**Rest Assured** é uma biblioteca Java open-source amplamente utilizada para testes de APIs REST. Oferece uma DSL fluente e expressiva para escrever testes robustos com validações poderosas.

### ✨ Principais Características

- **🔥 DSL Fluente**: Sintaxe `given().when().then()` intuitiva e legível
- **🚀 Suporte completo a REST**: GET, POST, PUT, PATCH, DELETE com JSON/XML
- **🎯 Assertions com Hamcrest**: Validações poderosas e expressivas nativas
- **🔄 Reutilização com RequestSpec**: Especificações reutilizáveis de requisição e resposta
- **📊 Integração com Allure**: Relatórios detalhados e visuais com `givenWithAllure()`
- **🧪 Data-Driven Testing**: Suporte nativo via `@CsvFileSource`, `@MethodSource`
- **⚡ Execução Paralela**: Suporte via `@Execution(CONCURRENT)` do JUnit 5
- **🔐 Autenticação**: Suporte para OAuth2, JWT, Basic Auth e headers customizados

### 💡 Por que usar Rest Assured?

1. **Produtividade**: DSL fluente reduz verbosidade em comparação ao HttpClient puro
2. **Manutenibilidade**: Testes legíveis e fáceis de manter
3. **Integração**: Funciona nativamente com JUnit 5, TestNG e Maven
4. **Comunidade ativa**: Amplamente adotado no mercado e bem documentado
5. **CI/CD**: Fácil integração com Jenkins, GitLab CI, GitHub Actions

---

## 🌐 Sobre a API ServeRest

[ServeRest](https://serverest.dev/) é uma API REST gratuita que simula uma loja virtual para fins educacionais e prática de testes de API.

### 🛍️ Endpoints Disponíveis

| Recurso | Endpoints | Descrição |
|---------|-----------|-----------|
| **Login** | `POST /login` | Autenticação de usuários |
| **Usuários** | `GET, POST, PUT, DELETE /usuarios` | Gerenciamento de usuários |
| **Produtos** | `GET, POST, PUT, DELETE /produtos` | Gerenciamento de produtos (requer admin) |
| **Carrinhos** | `GET, POST, DELETE /carrinhos` | Gerenciamento de carrinhos de compras |

### 🔗 Base URL

```
https://serverest.dev
```

### 📖 Documentação Completa

- **Swagger UI**: https://serverest.dev/
- **Repositório**: https://github.com/ServeRest/ServeRest
- **Front-end (Beta)**: https://front.serverest.dev/

---

## 📁 Estrutura do Projeto

```
karate_api_java/
│
├── src/
│   ├── main/
│   │   └── java/                         # (vazio neste projeto)
│   │
│   └── test/
│       ├── java/
│       │   └── restassured/
│       │       ├── base/
│       │       │   └── BaseApiTest.java        # Configuração base (RequestSpec, BaseURI)
│       │       ├── login/
│       │       │   └── LoginRestAssuredTest.java
│       │       ├── usuarios/
│       │       │   └── UsuariosRestAssuredTest.java
│       │       ├── produtos/
│       │       │   └── ProdutosRestAssuredTest.java
│       │       └── utils/
│       │           └── FakerUtils.java
│       │
│       └── resources/
│           ├── restassured/
│           │   └── login/
│           │       └── invalid-login-emails.csv
│           └── junit-platform.properties
│
├── pom.xml                               # Dependências Maven
└── README.md                             # Este arquivo
```

---

## 🔧 Pré-requisitos

- **Java JDK 21** (LTS) ou superior
- **Maven 3.6+**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)
- Conexão com internet (para acessar a API ServeRest)

### Download do Java 21

- Oracle JDK 21: https://www.oracle.com/java/technologies/downloads/#java21

### Verificar instalação

```bash
java -version
mvn -version
```

---

## 📦 Versões das dependências (pom.xml)

| Componente                         | Artefato                                           | Versão  |
|------------------------------------|----------------------------------------------------|---------|
| Rest Assured                       | io.rest-assured:rest-assured                       | 5.3.2   |
| Rest Assured JSON Path             | io.rest-assured:json-path                          | 5.3.2   |
| Allure Rest Assured                | io.qameta.allure:allure-rest-assured               | 2.24.0  |
| JUnit 5 (Jupiter API)             | org.junit.jupiter:junit-jupiter-api                | 5.10.1  |
| JUnit 5 (Jupiter Engine)          | org.junit.jupiter:junit-jupiter-engine             | 5.10.1  |
| JUnit 5 (Jupiter Params)          | org.junit.jupiter:junit-jupiter-params             | 5.10.1  |
| Hamcrest                           | org.hamcrest:hamcrest                              | 2.2     |
| Java Faker                         | com.github.javafaker:javafaker                     | 1.0.2   |
| Maven Surefire Plugin             | org.apache.maven.plugins:maven-surefire-plugin     | 3.2.5   |
| Maven Compiler Plugin             | org.apache.maven.plugins:maven-compiler-plugin     | 3.11.0  |

---

## 🚀 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/reinaldorossetti/karate_api_java.git
cd karate_api_java
```

### 2. Instale as dependências

```bash
mvn clean install
```

### 3. Verifique a instalação

```bash
mvn test
```

---

## ▶️ Executando os Testes

### Executar todos os testes do projeto restassured.

```bash
mvn clean test -Dtest=restassured.**.*Test
```

### Executar suíte de login

```bash
mvn -Dtest=LoginRestAssuredTest test
```

### Executar método específico

```bash
mvn -Dtest=LoginRestAssuredTest#ct01_loginWithValidCredentials test
```

### Execução paralela (JUnit 5)

Arquivo: `src/test/resources/junit-platform.properties`

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
junit.jupiter.execution.parallel.config.dynamic.factor=2
```

---

## 🔍 Análise Estática de Código (PMD & Checkstyle)

O projeto conta com ferramentas de auditoria e qualidade de código configuradas no `pom.xml`.

### 🛡️ Executando a Análise Estática com PMD

O **PMD 7.7.0** realiza a análise estática das classes de teste Java utilizando o conjunto de regras customizado [`pmd-ruleset.xml`](pmd-ruleset.xml).

Para rodar a verificação do PMD:

```bash
mvn pmd:check
```

- **Arquivo de Regras**: [`pmd-ruleset.xml`](pmd-ruleset.xml)
- **Relatório de Saída**: `target/pmd.xml`

#### ⚙️ Estrutura do `pmd-ruleset.xml`

O arquivo `pmd-ruleset.xml` estende os conjuntos padrão do PMD (`bestpractices`, `codestyle`, `errorprone`), aplicando exclusões específicas para testes de API com RestAssured:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ruleset name="Custom PMD Ruleset for ServeRest"
         xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

    <description>Regras customizadas do PMD ajustadas para suíte de testes de API com RestAssured e JUnit 5.</description>

    <!-- Exclusões de Best Practices para automação de testes REST -->
    <rule ref="category/java/bestpractices.xml">
        <exclude name="UnitTestAssertionsShouldIncludeMessage" />
        <exclude name="UnitTestShouldIncludeAssert" />
        <exclude name="JUnit5TestShouldBePackagePrivate" />
        <exclude name="UnitTestContainsTooManyAsserts" />
    </rule>

    <rule ref="category/java/codestyle.xml" />
    <rule ref="category/java/errorprone.xml" />
</ruleset>
```

## ⚙️ Esteira CI/CD - GitHub Actions

Este projeto possui integração contínua configurada no GitHub Actions em:

- Arquivo: `.github/workflows/api-tests.yml`

### 🔁 Quando a esteira é executada

- `push` em qualquer branch
- `pull_request` aberto ou atualizado

### 🧱 Passos principais do job `test`

1. **Checkout do código**
   - `actions/checkout@v4`
2. **Configuração do Java 21**
   - `actions/setup-java@v5` com:
     - `java-version: '21'`
     - `distribution: 'temurin'`
3. **Execução dos testes via Maven**
   - Comando: `mvn clean -Dtest=ExecutionBuilderRunner test`
4. **Publicação de relatórios Allure**
   - Publica `target/allure-report` via `peaceiris/actions-gh-pages@v4` na branch `gh-pages`, pasta `allure-reports`.

### 🌐 Acesso ao relatório no GitHub Pages

Após a execução da esteira:

- URL do relatório:
  - `https://reinaldorossetti.github.io/karate_api_java/allure-reports/index.html`

---

## 📝 Exemplos de Testes

### Exemplo 1: Configuração Base (BaseApiTest)

```java
public class BaseApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://serverest.dev";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification givenWithAllure() {
        return given()
                .filter(new AllureRestAssured());
    }
}
```

### Exemplo 2: Login com sucesso (GET/POST)

```java
@Test
@DisplayName("CT01 - Login com credenciais válidas e validação de token")
void ct01_loginWithValidCredentials() {
    String email = FakerUtils.randomEmail();
    String password = "SenhaSegura@123";

    // Cria usuário
    givenWithAllure()
        .body("{\"nome\":\"Teste\",\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"administrador\":\"true\"}")
    .when()
        .post("/usuarios")
    .then()
        .statusCode(201);

    // Realiza login
    givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
    .when()
        .post()
    .then()
        .statusCode(200)
        .body("message", equalTo("Login realizado com sucesso"))
        .body("authorization", notNullValue());
}
```

### Exemplo 3: Login inválido

```java
@Test
@DisplayName("CT02 - Login com credenciais inválidas")
void ct02_loginWithInvalidCredentials() {
    givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"invalido@teste.com\",\"password\":\"senhaerrada\"}")
    .when()
        .post()
    .then()
        .statusCode(401)
        .body("message", equalTo("Email e/ou senha inválidos"))
        .body("authorization", nullValue());
}
```

### Exemplo 4: Validação de campos obrigatórios

```java
@Test
@DisplayName("CT03 - Validação de campos obrigatórios no login")
void ct03_validateRequiredFields() {
    // Email vazio
    givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"\",\"password\":\"senha123\"}")
    .when()
        .post()
    .then()
        .statusCode(400)
        .body("email", notNullValue());

    // Senha vazia
    givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"test@email.com\",\"password\":\"\"}")
    .when()
        .post()
    .then()
        .statusCode(400)
        .body("password", notNullValue());
}
```

### Exemplo 5: Uso do token em rota protegida

```java
@Test
@DisplayName("CT04 - Login e uso de token em rota protegida")
void ct04_loginAndUseTokenInProtectedRoute() {
    String email = FakerUtils.randomEmail();
    String password = "SenhaSegura@123";

    // Cria usuário comum (não admin)
    givenWithAllure()
        .body("{\"nome\":\"Teste\",\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"administrador\":\"false\"}")
    .when()
        .post("/usuarios")
    .then()
        .statusCode(201);

    // Extrai token
    String token = givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
    .when()
        .post()
    .then()
        .statusCode(200)
        .extract().path("authorization");

    // Tenta criar produto sem permissão de admin
    givenWithAllure()
        .header("Authorization", token)
        .basePath("/produtos")
        .body("{\"nome\":\"Produto Teste\",\"preco\":100,\"descricao\":\"Teste\",\"quantidade\":10}")
    .when()
        .post()
    .then()
        .statusCode(403)
        .body("message", equalTo("Rota exclusiva para administradores"));
}
```

### Exemplo 6: Teste parametrizado com CSV

```java
@ParameterizedTest(name = "CT05 - Email inválido: {0}")
@CsvFileSource(resources = "/restassured/login/invalid-login-emails.csv", numLinesToSkip = 1)
@Execution(ExecutionMode.CONCURRENT)
@DisplayName("CT05 - Validação de formato de e-mail inválido")
void ct05_validateInvalidEmailFormat(String invalidEmail) {
    givenWithAllure()
        .basePath("/login")
        .body("{\"email\":\"" + invalidEmail + "\",\"password\":\"senha123\"}")
    .when()
        .post()
    .then()
        .statusCode(400)
        .body("email", notNullValue());
}
```

### Arquivo CSV utilizado no CT05

`src/test/resources/restassured/login/invalid-login-emails.csv`

```csv
invalidEmail
emailwithoutat
@noname.com
email@nodomain
email
12345@test.c
!@#$%
```

---

## 🎯 Funcionalidades do Rest Assured

### 1. RequestSpecification reutilizável

```java
RequestSpecification requestSpec = new RequestSpecBuilder()
    .setBaseUri("https://serverest.dev")
    .setContentType(ContentType.JSON)
    .addFilter(new AllureRestAssured())
    .build();

given()
    .spec(requestSpec)
    .body(payload)
.when()
    .post("/login")
.then()
    .statusCode(200);
```

### 2. Extração de valores da resposta

```java
// Extrair campo simples
String token = response.path("authorization");

// Extrair com extract()
String token = given()
    .body(payload)
.when()
    .post("/login")
.then()
    .extract().path("authorization");

// Extrair response completa
Response response = given()
    .body(payload)
.when()
    .post("/login")
.then()
    .extract().response();
```

### 3. Validações com Hamcrest

```java
.then()
    .statusCode(200)
    // Igualdade
    .body("message", equalTo("Login realizado com sucesso"))
    // Não nulo
    .body("authorization", notNullValue())
    // Nulo
    .body("error", nullValue())
    // Contém string
    .body("message", containsString("sucesso"))
    // Lista não vazia
    .body("usuarios", not(empty()))
    // Tamanho de lista
    .body("usuarios.size()", greaterThan(0));
```

### 4. Variáveis e Reutilização

```java
// Dados dinâmicos com Faker
String email    = FakerUtils.randomEmail();
String name     = FakerUtils.randomName();
String product  = FakerUtils.randomProduct();

// Montar payload dinâmico
String payload = String.format(
    "{\"email\":\"%s\",\"password\":\"%s\"}", email, password
);

// Reutilizar token entre testes
String token = loginAndGetToken(email, password);
given().header("Authorization", token).when().get("/produtos");
```

### 5. Hooks e Setup com JUnit 5

```java
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class LoginRestAssuredTest extends BaseApiTest {

    @BeforeAll
    void init() {
        RestAssured.baseURI = "https://serverest.dev";
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.println("Executando: " + testInfo.getDisplayName());
    }
}
```

---

## 📊 Relatórios

Após executar os testes, relatórios Allure são gerados automaticamente:

```
target/allure-report/index.html
```

Para gerar e abrir o relatório localmente:

```bash
mvn allure:serve
```

O relatório exibe:
- ✅ Testes executados, passados e falhos
- ⏱️ Tempo de execução por teste
- 📋 Logs detalhados de request/response
- 📊 Gráficos de cobertura e histórico de execuções

---

## 🎓 Boas Práticas

### 1. Organização das Classes de Teste

- ✅ Uma classe por recurso (`LoginRestAssuredTest`, `UsuariosRestAssuredTest`)
- ✅ Use `BaseApiTest` para configurações comuns
- ✅ Nomeie os métodos de forma descritiva com prefixo `ct0X_`

### 2. Anotações para Organização

```java
@Tag("smoke")       // Testes críticos de smoke
@Tag("regression")  // Suite de regressão completa
@Tag("login")       // Testes de login
@Disabled           // Temporariamente desabilitado
```

### 3. Reutilização de Código

- ✅ Crie métodos helper (ex: `loginAndGetToken()`, `createUser()`)
- ✅ Use `RequestSpecification` para configurações comuns
- ✅ Centralize constantes em classes utilitárias

### 4. Dados de Teste

```java
// Sempre use dados dinâmicos para evitar conflitos
String email    = FakerUtils.randomEmail();
String product  = FakerUtils.randomProduct() + System.currentTimeMillis();

// Evite dados estáticos hardcoded que podem causar conflitos
```

### 5. Validações Completas

```java
// Sempre valide:
.statusCode(200)                             // Status code
.body("message", equalTo("..."))             // Mensagem de resposta
.body("authorization", notNullValue())       // Campos obrigatórios
.header("Content-Type", containsString("application/json")) // Headers
```

---

## 📚 Recursos Adicionais

### Documentação Oficial

- 🧪 **Rest Assured**: https://rest-assured.io/
- 🌐 **ServeRest**: https://serverest.dev/
- ☕ **JUnit 5**: https://junit.org/junit5/
- 📊 **Allure**: https://docs.qameta.io/allure/

### Tutoriais e Cursos

- [Rest Assured Official Documentation](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/javadoc/2.2/)
- [ServeRest GitHub](https://github.com/ServeRest/ServeRest)

### Comunidade

- [Stack Overflow - Rest Assured Tag](https://stackoverflow.com/questions/tagged/rest-assured)
- [Rest Assured GitHub Issues](https://github.com/rest-assured/rest-assured/issues)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autor

Desenvolvido para fins de estudo e prática de automação de testes de API.

---

Referências:
- https://rest-assured.io/
- https://junit.org/junit5/
- https://github.com/rest-assured/rest-assured/wiki/Usage

**🚀 Happy Testing!** 🧪