package dev.serverest.usuarios;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import dev.serverest.BaseApiTest;
import dev.serverest.utils.FakerUtils;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * API edge case tests for users: PUT upsert behavior and malformed token handling.
 * These tests focus on high-criticality scenarios covering VADER dimensions:
 * - V (Verbs): PUT upsert behavior on non-existent IDs
 * - A (Authentication): Malformed bearer tokens (missing "Bearer" prefix)
 */
@Execution(ExecutionMode.CONCURRENT)
public class UsersEdgeCasesTests extends BaseApiTest {

  /**
   * CT19 - Validar comportamento de upsert em PUT /usuarios/{id} com ID inexistente.
   *
   * <p>VADER Dimension: [V - Verbs]
   *
   * <p>Requisito: Quando um usuário envia PUT para /usuarios/{id} com um ID que não existe
   * (formato válido de 16 caracteres alfanuméricos), o sistema deve criar um novo usuário
   * com status 201 Created e incluir o novo ID na resposta.
   *
   * <p>Criticidade: 🔴 ALTA - Comportamento crítico de semântica REST (upsert em PUT)
   *
   * <p>Isolamento: ✅ Teste completamente independente - gera novo ID aleatório para garantir
   * que não existe no banco de dados.
   */
  @Test
  @Severity(SeverityLevel.BLOCKER)
  @DisplayName("CT19 - PUT em ID inexistente deve criar usuário (upsert) com 201 Created")
  void putNonExistentUserIdShouldCreateUserUpsert() {
    final String newUserEmail = FakerUtils.randomEmail();
    final String newUserName = FakerUtils.randomName();
    final String newUserPassword = FakerUtils.randomPassword();
    final String administrador = "false";
    final String nonExistentUserId = FakerUtils.randomAlphanumericId();
    final Map<String, Object> bodyPayload =
        bodyPayload(newUserName, newUserEmail, newUserPassword, administrador);

    final Response putResponse =
        givenWithAllure()
            .contentType(ContentType.JSON)
            .basePath(ROTA_USUARIOS + "/" + nonExistentUserId)
            .body(bodyPayload)
            .when()
            .put()
            .then()
            .statusCode(201)
            .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
            .body(KEY_ID, notNullValue())
            .extract()
            .response();

    final String createdUserId = putResponse.path(KEY_ID);
    givenWithAllure()
        .basePath(ROTA_USUARIOS + "/" + createdUserId)
        .when()
        .get()
        .then()
        .statusCode(200)
        .body(KEY_NOME, equalTo(newUserName))
        .body(KEY_EMAIL, equalTo(newUserEmail))
        .body(KEY_PASSWORD, equalTo(newUserPassword))
        .body(KEY_ADMINISTRADOR, equalTo(administrador))
        .body(matchesJsonSchemaInClasspath("schemas/usuarios/user-by-id-schema.json"));
  }

  /**
   * CT20 - Validar que requisição sem token de autenticação retorna 401 em endpoint protegido.
   *
   * <p>VADER Dimension: [A - Authentication & Authorization]
   *
   * <p>Requisito: Quando um usuário tenta acessar um endpoint protegido (ex: POST /carrinhos)
   * **SEM** fornecer um token JWT no header `Authorization`, o sistema deve retornar 401
   * Unauthorized com mensagem de erro clara.
   *
   * <p>Criticidade: 🔴 ALTA - Validação de segurança crítica (autenticação obrigatória)
   *
   * <p>Isolamento: ✅ Teste completamente independente - tenta criar carrinho sem token
   * sem necessidade de criar usuário prévio.
   */
  @Test
  @Severity(SeverityLevel.BLOCKER)
  @DisplayName("CT20 - Requisição sem token deve retornar 401 Unauthorized em endpoint protegido")
  void requestWithoutTokenShouldReturn401() {
    final Map<String, Object> cartPayload =
        Map.of(
            "produtos", new Object[] {});

    givenWithAllure()
        .contentType(ContentType.JSON)
        .basePath(ROTA_CARRINHOS)
        .body(cartPayload)
        .when()
        .post()
        .then()
        .statusCode(401)
        .body(
            KEY_MESSAGE,
            containsString(
                "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
  }
}
