---
inclusion: always
---

# Technology Stack & Constraints

## Arquivos de Configuração de Referência
- #[[file:pom.xml]]
- #[[file:pmd-ruleset.xml]]
- #[[file:checkstyle-suppressions.xml]]

## Linguagens & Runtime

- **Java**: 23+ (LTS recomendada para produção)
- **Maven**: 3.6+ (wrapper `./mvnw` incluído)
- **Shell**: PowerShell (Windows), Bash (Linux/macOS)

## Frameworks de Teste Homologados

| Framework | Versão | Propósito |
|-----------|--------|----------|
| **Rest Assured** | 6.1.0 | Biblioteca principal para testes de API REST |
| **JUnit 5** | 5.10.1+ | Framework de execução de testes |
| **JUnit Platform** | 1.10.1+ | Suporte a suítes parametrizadas |
| **Allure** | 2.35.5+ | Relatórios interativos com anexos HTTP |
| **JMeter DSL** | 2.2 | Testes de carga (NFR) |
| **Datafaker** | 2.7.0+ | Geração de dados dinâmicos |

## Bibliotecas Utilitárias Autorizadas

| Dependência | Versão | Uso |
|-------------|--------|-----|
| `io.rest-assured:json-schema-validator` | 6.1.0 | Validação de contrato JSON Schema |
| `com.fasterxml.jackson:jackson-bom` | 2.22.3+ | Serialização/desserialização JSON |
| `org.codehaus.groovy:groovy` | 3.0.25+ | Suporte a Groovy no Rest Assured |
| `com.thoughtworks.xstream:xstream` | 1.4.21+ | Serialização XML (se necessário) |
| `net.minidev:json-smart` | 2.5.2+ | Parsing JSON otimizado |

## Plugins Maven Obrigatórios

```xml
<!-- Análise Estática de Código -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-pmd-plugin</artifactId>
  <version>3.26.0</version>
  <configuration>
    <rulesets>
      <ruleset>${project.basedir}/pmd-ruleset.xml</ruleset>
    </rulesets>
  </configuration>
</plugin>

<!-- Relatórios Allure -->
<plugin>
  <groupId>io.qameta.allure</groupId>
  <artifactId>allure-maven</artifactId>
  <version>2.17.0</version>
</plugin>

<!-- Verificação de Estilo (Checkstyle) -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.3.1</version>
  <configuration>
    <configLocation>google_checks.xml</configLocation>
    <suppressionsLocation>checkstyle-suppressions.xml</suppressionsLocation>
  </configuration>
</plugin>
```

## Restrições Técnicas

### ❌ PROIBIDO
- Dependências não homologadas sem aprovação arquitetural
- Drivers de navegador (Selenium, Playwright, Cypress) — projeto é API-only
- Mocking inline sem frameworks (use RestAssured + WireMock se necessário)
- Código procedural sem modularização (violação DRY)
- Testes com dependência entre casos (estado compartilhado)

### ✅ PERMITIDO
- Extensão de `BaseApiTest` para novas suítes
- Uso de `@ParameterizedTest` + `@CsvFileSource`
- Integração com Allure via `AllureRestAssured` + `@Attachment`
- Builders/Fluent API para payloads complexos
- Padrão Page Object (adaptado para API: SerializationHelper, Fixtures)

## Ambientes Suportados

| Ambiente | URL | Uso |
|----------|-----|-----|
| **Produção** | `https://serverest.dev` | Execução local e testes de integração |
| **Desenvolvimento (CI)** | `http://localhost:3000` (Docker) | GitHub Actions (Ubuntu runner) |
| **Documento de Especificação** | [Swagger ServeRest](https://serverest.dev/#/) | Referência de API |

## Variáveis de Ambiente

```bash
# .env na raiz do projeto
BASE_URL_PROD=https://serverest.dev
BASE_URL_DEV=http://localhost:3000
CI=true|false  # Detectado automaticamente no GitHub Actions
```

## Executáveis & Validação

### Validar Ambiente

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -v

# Verificar Docker (se usar CI localmente)
docker --version
```

### Comandos Build Padrão

```bash
# Limpar, compilar, testar
mvn clean test

# Apenas validar (Checkstyle + PMD)
mvn validate pmd:check checkstyle:check

# Gerar relatório Allure
mvn allure:serve

# Executar uma suíte específica
mvn "-Dtest=dev.serverest.usuarios.UsersFeatureTests" test
```
