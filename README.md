# 🧪 Rest Assured API Testing - ServeRest

[![RestAssured](https://img.shields.io/badge/RestAssured-6.0.0-blue.svg)](https://rest-assured.io/)
[![JUnit5](https://img.shields.io/badge/JUnit-5.10.1-green.svg)](https://junit.org/junit5/)
[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://www.oracle.com/java/)
[![PMD](https://img.shields.io/badge/PMD-7.7.0-brightgreen.svg)](https://pmd.github.io/)

Projeto de automação de testes de API utilizando **Rest Assured** e **Java 23** para validar a API REST **ServeRest** — uma plataforma que simula os serviços de um e-commerce.

Estruturado com boas práticas de engenharia de software (Clean Code, SOLID), relatórios interativos com **Allure Report**, análise estática via **PMD 7** e esteira de CI/CD via **GitHub Actions**.

- **Repositório**: [https://github.com/reinaldorossetti/serverest_restassured_java](https://github.com/reinaldorossetti/serverest_restassured_java)
- **Relatório no GitHub Pages**: [https://reinaldorossetti.github.io/serverest_restassured_java/allure-report/](https://reinaldorossetti.github.io/serverest_restassured_java/allure-reports/index.html)
- **Mapeamento de Testes**: [TESTING_API.MD](TESTING_API.MD)
- **Requisitos do Projeto**: [Requisitos.md](Requisitos.md)

---

## 📚 Índice

- [Sobre o Rest Assured](#-sobre-o-rest-assured)
- [Sobre a API ServeRest](#-sobre-a-api-serverest)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Pré-requisitos](#-pré-requisitos)
- [Dependências do Projeto (pom.xml)](#-dependências-do-projeto-pomxml)
- [Instalação e Execução](#-instalação-e-execução)
- [Análise Estática de Código (PMD)](#-análise-estática-de-código-pmd)
- [Esteira CI/CD - GitHub Actions](#-esteira-cicd---github-actions)
- [Exemplos de Testes](#-exemplos-de-testes)
- [Relatórios Allure](#-relatórios-allure)
- [Boas Práticas](#-boas-práticas)

---

## 🧪 Sobre o Rest Assured

**Rest Assured** é uma biblioteca Java open-source amplamente utilizada para automação de testes de APIs REST. 
Ela oferece uma DSL (Domain Specific Language) fluente baseada no padrão BDD `given().when().then()`.

### ✨ Principais Características

- **🔥 DSL Fluente**: Sintaxe `given().when().then()` intuitiva e legível
- **🚀 Suporte completo a REST**: GET, POST, PUT, DELETE com payloads JSON
- **🎯 Assertions com Hamcrest**: Validações estruturais e de schema
- **📊 Integração com Allure**: Logs automáticos de requisições e respostas com `AllureRestAssured`
- **🧪 Data-Driven Testing**: Suporte a testes parametrizados via `@CsvFileSource`
- **⚡ Execução Paralela**: Suporte habilitado via JUnit 5 Platform
- **🔐 Autenticação**: Validação e transmissão de tokens JWT em cabeçalhos HTTP

---

## 🌐 Sobre a API ServeRest

[ServeRest](https://serverest.dev/) é uma API REST gratuita que simula uma loja virtual para fins educacionais e prática de testes de API.

### 🛍️ Endpoints Cobertos

| Recurso | Endpoints | Descrição |
|---------|-----------|-----------|
| **Login** | `POST /login` | Autenticação e geração de token JWT |
| **Usuários** | `GET, POST, PUT, DELETE /usuarios` | Gerenciamento de cadastro de usuários |

### 🔗 Base URL
```
https://serverest.dev
```

---

## 📁 Estrutura do Projeto

```
serverest_restassured_java/
│
├── src/
│   └── test/
│       ├── java/
│       │   └── restassured_serverest/
│       │       ├── BaseApiTest.java               # Configurações base (BaseURI, RequestSpecs, Filtro Allure)
│       │       ├── ExecutionBuilderRunner.java    # Suíte runner de execução dos testes JUnit
│       │       ├── login/
│       │       │   └── LoginRestAssuredTest.java  # Suíte de testes do recurso Login
│       │       ├── usuarios/
│       │       │   └── UsersRestAssuredTest.java  # Suíte de testes do recurso Usuários
│       │       └── utils/
│       │           └── FakerUtils.java            # Utilitário para geração de dados dinâmicos
│       │
│       └── resources/
│           ├── logback-test.xml                   # Configuração de logs do projeto
│           └── restassured/
│               └── login/
│                   └── invalido-login.csv         # Massa de dados para teste parametrizado
│
├── pmd-ruleset.xml                               # Regras customizadas da análise estática PMD 7
├── pom.xml                                       # Gerenciamento de dependências e plugins Maven
├── Requisitos.md                                 # Especificações dos requisitos do projeto
├── TESTING_API.MD                                # Mapeamento completo dos cenários de teste
└── README.md                                     # Documentação principal
```

---

## 🔧 Pré-requisitos

- **Java JDK 23**
- **Maven 3.8+** (ou o Maven Wrapper `./mvnw` incluso)
- **IDE** (VS Code, IntelliJ IDEA ou Eclipse)

---

## 📦 Dependências do Projeto (`pom.xml`)

| Componente | Grupo / Artefato | Versão |
|------------|------------------|--------|
| **Java** | `compiler.source / target` | `23` |
| **Rest Assured** | `io.rest-assured:rest-assured` | `6.0.0` |
| **JUnit 5** | `org.junit.jupiter:junit-jupiter-api` | `5.10.1` |
| **JUnit Platform** | `org.junit.platform:junit-platform-suite-api` | `1.10.1` |
| **Allure Rest Assured** | `io.qameta.allure:allure-rest-assured` | `2.17.0` |
| **Allure JUnit 5** | `io.qameta.allure:allure-junit5` | `2.17.0` |
| **Java Faker** | `com.github.javafaker:javafaker` | `1.0.2` |
| **Maven PMD Plugin** | `org.apache.maven.plugins:maven-pmd-plugin` | `3.26.0` (PMD 7.7.0) |

---

## 🚀 Instalação e Execução

### 1. Clonar o repositório
```bash
git clone https://github.com/reinaldorossetti/serverest_restassured_java.git
cd serverest_restassured_java
```

### 2. Executar todos os testes
```bash
mvn clean test
```

### 3. Executar uma suíte específica
```bash
mvn test -Dtest=UsersRestAssuredTest
```

### 4. Executar via Runner de Testes
```bash
mvn test -Dtest=ExecutionBuilderRunner
```

---

## 🔍 Análise Estática de Código (PMD)

O projeto conta com validação automatizada de qualidade de código utilizando o **PMD 7.7.0**.

### Executar auditoria com PMD
```bash
mvn pmd:check
```

- **Arquivo de Regras Customizado**: [`pmd-ruleset.xml`](pmd-ruleset.xml)
- **Relatório de Saída**: `target/pmd.xml`

---

## ⚙️ Esteira CI/CD - GitHub Actions

A integração contínua é executada automaticamente no GitHub Actions através do workflow `.github/workflows/api-tests.yml`.

### Passos da Esteira:
1. Checkout do código-fonte.
2. Configuração do ambiente **Java 23**.
3. Execução dos testes automatizados via Maven.
4. Geração do relatório **Allure Report**.
5. Publicação automática no **GitHub Pages**.

---

## 📝 Exemplos de Testes

### Exemplo 1: Configuração Base (`BaseApiTest`)

```java
public abstract class BaseApiTest {

    public static final String ROTA_USUARIOS = "/usuarios";
    public static final String ROTA_LOGIN = "/login";

    protected RequestSpecification givenWithAllure() {
        return RestAssured.given().filter(new AllureRestAssured());
    }

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.baseURI = "https://serverest.dev";
    }
}
```

### Exemplo 2: Teste de Login com Sucesso (`LoginRestAssuredTest`)

```java
@Test
@DisplayName("CT01 - Login com credenciais válidas")
void loginWithValidCredentials() {
    final String userEmail = FakerUtils.randomEmail();
    final String userPassword = "SenhaSegura@123";

    createUser(userEmail, userPassword, true)
            .then()
            .statusCode(201);

    givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_LOGIN)
            .body("{\"email\": \"" + userEmail + "\", \"password\": \"" + userPassword + "\"}")
            .when()
            .post()
            .then()
            .statusCode(200)
            .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
            .body(HEADER_AUTHORIZATION, notNullValue());
}
```

### Exemplo 3: Teste Parametrizado com CSV (`LoginRestAssuredTest`)

```java
@ParameterizedTest(name = "CT05 - Validar e-mail com formato inválido: {0}")
@CsvFileSource(resources = "/restassured/login/invalido-login.csv", numLinesToSkip = 1)
@DisplayName("CT05 - Validação de formato de e-mail inválido")
void validateInvalidEmailFormat(String invalidEmail) {
    givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_LOGIN)
            .body("{\"email\": \"" + invalidEmail + "\", \"password\": \"SenhaSegura@123\"}")
            .when()
            .post()
            .then()
            .statusCode(400)
            .body(KEY_EMAIL, equalTo("email deve ser um email válido"));
}
```

---

## 📊 Relatórios Allure

Para gerar e abrir o relatório visual localmente após a execução dos testes:

```bash
mvn allure:serve
```

O relatório interativo exibe:
- Status de aprovação dos testes.
- Anexo completo de requisições e respostas HTTP (corpo, headers, status code).
- Gráficos de tempo de execução e cobertura das suítes.

---

## 🎓 Boas Práticas Aplicadas

1. **Separação por Recurso**: Suítes isoladas por domínio ([UsersRestAssuredTest](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/usuarios/UsersRestAssuredTest.java), [ProductsRestAssuredTest](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/produtos/ProductsRestAssuredTest.java), [CartsRestAssuredTest](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/carrinhos/CartsRestAssuredTest.java), [LoginRestAssuredTest](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/login/LoginRestAssuredTest.java)).
2. **Dados Dinâmicos**: Uso de [FakerUtils.java](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/utils/FakerUtils.java) para gerar massas de teste sem causar colisões.
3. **Padrão DRY (Don't Repeat Yourself)**: Reutilização de especificações na classe base [BaseApiTest.java](file:///d:/github-projects/serverest_restassured_java/src/test/java/restassured_serverest/BaseApiTest.java).
4. **Respeito aos Princípios SOLID & Clean Code**: Métodos pequenos, tipagem estrita e nomes descritivos.

5. **Validações de API**: Seguindo as melhores práticas de validação de APIs REST, conforme detalhado neste artigo:
https://reiload-88128.medium.com/quais-validações-devo-realizar-em-uma-api-postman-ca99eeae81dd

---

## 👨‍💻 Autor

Desenvolvido por **Reinaldo Rossetti** para automação de testes de API REST em Java.