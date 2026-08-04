package restassured_serverest.login;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import restassured_serverest.BaseApiTest;
import restassured_serverest.utils.FakerUtils;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class LoginRestAssuredTest extends BaseApiTest {

    private Response createUser(final String email, final String password, final boolean admin) {
        final String payload = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"%s\"\n}",
                email, email, password, admin ? "true" : "false");

        return givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(payload)
                .when()
                .post();
    }

    @Test
    @DisplayName("CT01 - Realizar login com credenciais válidas e validar token")
    void loginWithValidCredentials() {
        final String email = FakerUtils.randomEmail();
        final String password = "SenhaSegura@123";

        createUser(email, password, false)
                .then()
                .statusCode(201);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
                .body("authorization", notNullValue());
    }

    @Test
    @DisplayName("CT02 - Tentar login com credenciais inválidas")
    void loginWithInvalidCredentials() {
        final String body = "{\n  \"email\": \"usuario@inexistente.com\",\n  \"password\": \"senhaerrada\"\n}";

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
    @DisplayName("CT03 - Validar campos obrigatórios no login")
    void validateRequiredFields() {
        // 1) Email vazio, senha preenchida
        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"\", \"password\": \"senha123\"}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_EMAIL, notNullValue());

        // 2) Email preenchido, senha vazia
        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"test@email.com\", \"password\": \"\"}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("password", notNullValue());

        // 3) Ambos vazios
        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"\", \"password\": \"\"}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_EMAIL, notNullValue())
                .body("password", notNullValue());
    }

    @Test
    @DisplayName("CT04 - Login e uso do token para acessar recurso protegido")
    void loginAndUseTokenInProtectedRoute() {
        // Cria usuário comum (não administrador)
        final String userEmail = FakerUtils.randomEmail();
        final String userPassword = "SenhaSegura@123";

        createUser(userEmail, userPassword, false)
                .then()
                .statusCode(201);

        // Faz login com usuário comum
        final Response loginResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"" + userEmail + "\", \"password\": \"" + userPassword + "\"}")
                .when()
                .post()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Login realizado com sucesso"))
                .extract().response();

        final String authToken = loginResponse.path("authorization");

        // Tenta acessar rota protegida de criação de produto
        final String productName = FakerUtils.randomProduct();
        final String productPayload = String.format(
                "{\n  \"nome\": \"%s\",\n  \"preco\": 100,\n  \"descricao\": \"Produto gerado com Faker para teste de autenticacao\",\n  \"quantidade\": 10\n}",
                productName);

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
    @DisplayName("CT05 - Validar formato de e-mail inválido")
    void validateInvalidEmailFormat(final String invalidEmail) {
        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body("{\"email\": \"" + invalidEmail + "\", \"password\": \"senha123\"}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_EMAIL, notNullValue())
                .body(KEY_EMAIL, containsString(""));
    }
}
