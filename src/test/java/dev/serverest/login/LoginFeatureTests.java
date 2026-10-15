package dev.serverest.login;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.serverest.BaseApiTest;
import dev.serverest.utils.FakerUtils;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/** API tests for user authentication. */
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class LoginFeatureTests extends BaseApiTest {

  private Response createUser(final String email, final String password, final boolean admin) {
    final Map<String, Object> payload =
        Map.of(
            KEY_NOME, email,
            KEY_EMAIL, email,
            KEY_PASSWORD, password,
            KEY_ADMINISTRADOR, admin ? "true" : "false");
    return givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_USUARIOS)
        .body(payload)
        .when()
        .post();
  }

  @Test
  @Severity(SeverityLevel.BLOCKER)
  @DisplayName("CT01 - Realizar login com credenciais válidas e validar token")
  void loginWithValidCredentials() {
    final String email = FakerUtils.randomEmail();
    createUser(email, DEFAULT_PASSWORD, false).then().statusCode(201);

    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of(KEY_EMAIL, email, KEY_PASSWORD, DEFAULT_PASSWORD))
        .when()
        .post()
        .then()
        .statusCode(200)
        .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
        .body("authorization", notNullValue());
  }

  @Test
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("CT02 - Tentar login com credenciais inválidas")
  void loginWithInvalidCredentials() {
    final Map<String, Object> body =
        Map.of(KEY_EMAIL, "usuario@inexistente.com", KEY_PASSWORD, "senhaerrada");
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(body)
        .when()
        .post()
        .then()
        .statusCode(401)
        .body(KEY_MESSAGE, equalTo("Email e/ou senha inválidos"))
        .body("authorization", nullValue());
  }

  @Test
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("CT03 - Validar campos obrigatórios no login")
  void validateRequiredFields() {
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of(KEY_EMAIL, "", KEY_PASSWORD, "senha123"))
        .when()
        .post()
        .then()
        .statusCode(400)
        .body(KEY_EMAIL, notNullValue());

    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of(KEY_EMAIL, "test@email.com", KEY_PASSWORD, ""))
        .when()
        .post()
        .then()
        .statusCode(400)
        .body("password", notNullValue());

    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of(KEY_EMAIL, "", KEY_PASSWORD, ""))
        .when()
        .post()
        .then()
        .statusCode(400)
        .body(KEY_EMAIL, notNullValue())
        .body("password", notNullValue());
  }

  @Test
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("CT04 - Login e uso do token para acessar recurso protegido")
  void loginAndUseTokenInProtectedRoute() {
    final String userEmail = FakerUtils.randomEmail();
    final String userPassword = "SenhaSegura@123";
    createUser(userEmail, userPassword, false).then().statusCode(201);

    final Response loginResponse =
        givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_LOGIN)
            .body(Map.of(KEY_EMAIL, userEmail, KEY_PASSWORD, userPassword))
            .when()
            .post()
            .then()
            .statusCode(200)
            .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
            .extract()
            .response();
    final String authToken = loginResponse.path("authorization");

    // Tenta acessar rota protegida de criação de produto
    final String productName = FakerUtils.randomProduct();
    final Map<String, Object> productPayload =
        Map.of(
            KEY_NOME, productName,
            "preco", 100,
            "descricao", "Produto gerado com Faker para teste de autenticacao",
            "quantidade", 10);
    givenWithAllure()
        .contentType(ContentType.JSON)
        .header(HEADER_AUTHORIZATION, authToken)
        .basePath(ROTA_PRODUTOS)
        .body(productPayload)
        .when()
        .post()
        .then()
        .statusCode(403)
        .body(KEY_MESSAGE, equalTo("Rota exclusiva para administradores"));
  }

  @ParameterizedTest(name = "CT05 - Validar formato de e-mail inválido: {0}")
  @CsvFileSource(resources = "/restassured/login/invalid-login-emails.csv", numLinesToSkip = 1)
  @Execution(ExecutionMode.CONCURRENT)
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("CT05 - Validar formato de e-mail inválido")
  void validateInvalidEmailFormat(final String invalidEmail) {
    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_LOGIN)
        .body(Map.of(KEY_EMAIL, invalidEmail, KEY_PASSWORD, "senha123"))
        .when()
        .post()
        .then()
        .statusCode(400)
        .body(KEY_EMAIL, notNullValue())
        .body(KEY_EMAIL, containsString(""));
  }
}
