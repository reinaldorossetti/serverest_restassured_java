package dev.serverest;

import dev.serverest.utils.FakerUtils;
import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

/** Shared setup and payload helpers for ServeRest API tests. */
public abstract class BaseApiTest {

  public static final String ROTA_USUARIOS = "/usuarios";
  public static final String ROTA_LOGIN = "/login";
  public static final String ROTA_CARRINHOS = "/carrinhos";
  public static final String ROTA_PRODUTOS = "/produtos";
  public static final String ROTA_CANCELAR_COMPRA = "/carrinhos/cancelar-compra";
  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String KEY_ID = "_id";
  public static final String KEY_MESSAGE = "message";
  public static final String KEY_EMAIL = "email";
  public static final String KEY_NOME = "nome";
  public static final String KEY_PASSWORD = "password";
  public static final String KEY_ADMINISTRADOR = "administrador";
  public static final String DEFAULT_PASSWORD = "SenhaSegura@123";
  public static final String PASSWORD = DEFAULT_PASSWORD;

  private static final Dotenv DOTENV =
      Dotenv.configure()
          .directory("./.env")
          .ignoreIfMalformed()
          .ignoreIfMissing()
          .load();

  /** Provides payloads with each required user field omitted or empty. */
  public static Stream<Arguments> invalidRequiredFieldsPayloads() {
    return Stream.of(
        Arguments.of(
            "nome ausente",
            "{\n  \"email\": \"required.nome.absent@test.com\",\n  \"password\": "
                + "\"Senha123@\",\n  \"administrador\": \"false\"\n}",
            "nome"),
        Arguments.of(
            "email ausente",
            "{\n  \"nome\": \"Usuário sem email\",\n  \"password\": "
                + "\"Senha123@\",\n  \"administrador\": \"false\"\n}",
            "email"),
        Arguments.of(
            "password ausente",
            "{\n  \"nome\": \"Usuário sem password\",\n  \"email\": "
                + "\"required.password.absent@test.com\",\n  \"administrador\": "
                + "\"false\"\n}",
            "password"),
        Arguments.of(
            "administrador ausente",
            "{\n  \"nome\": \"Usuário sem admin\",\n  \"email\": "
                + "\"required.admin.absent@test.com\",\n  \"password\": "
                + "\"Senha123@\"\n}",
            "administrador"),
        Arguments.of(
            "nome vazio",
            "{\n  \"nome\": \"\",\n  \"email\": \"required.nome.empty@test.com\",\n  "
                + "\"password\": \"Senha123@\",\n  \"administrador\": "
                + "\"false\"\n}",
            "nome"),
        Arguments.of(
            "email vazio",
            "{\n  \"nome\": \"Usuário email vazio\",\n  \"email\": "
                + "\"\",\n  \"password\": \"Senha123@\",\n  \"administrador\": "
                + "\"false\"\n}",
            "email"),
        Arguments.of(
            "password vazio",
            "{\n  \"nome\": \"Usuário password vazio\",\n  \"email\": "
                + "\"required.password.empty@test.com\",\n  \"password\": "
                + "\"\",\n  \"administrador\": \"false\"\n}",
            "password"),
        Arguments.of(
            "administrador vazio",
            "{\n  \"nome\": \"Usuário admin vazio\",\n  \"email\": "
                + "\"required.admin.empty@test.com\",\n  \"password\": "
                + "\"Senha123@\",\n  \"administrador\": \"\"\n}",
            "administrador"));
  }

  protected RequestSpecification givenWithAllure() {
    return RestAssured.given().filter(new AllureRestAssured());
  }

  /**
   * Builds the default payload for creating or updating a user.
   *
   * @param name user's name
   * @param email user's email
   * @param password user's password
   * @param administrador administrator flag ({@code "true"} or {@code "false"})
   * @return payload expected by the users API
   */
  protected Map<String, Object> bodyPayload(
      final String name, final String email, final String password, final String administrador) {
    return Map.of(
        KEY_NOME, name,
        KEY_EMAIL, email,
        KEY_PASSWORD, password,
        KEY_ADMINISTRADOR, administrador);
  }

  /**
   * Builds a user payload with the supplied email and random name/password.
   *
   * @param email email to use in the payload
   * @return user payload with {@code administrador = "false"}
   */
  protected Map<String, Object> bodyEmail(final String email) {
    final String name = FakerUtils.randomName();
    final String password = FakerUtils.randomPassword();

    return Map.of(
        KEY_NOME, name,
        KEY_EMAIL, email,
        KEY_PASSWORD, password,
        KEY_ADMINISTRADOR, "false");
  }

  /**
   * Builds a user payload with a fixed name for authentication/cart scenarios.
   *
   * @param email user's email
   * @param password user's password
   * @return user payload with {@code administrador = "true"}
   */
  protected Map<String, Object> bodyEmailAndPassword(final String email, final String password) {
    return Map.of(
        KEY_NOME, "Cart Default User",
        KEY_EMAIL, email,
        KEY_PASSWORD, password,
        KEY_ADMINISTRADOR, "true");
  }

  /**
   * Configures the global RestAssured {@code baseURI} before test execution.
   *
   * <p>When {@code CI=true}, uses {@code BASE_URL_DEV} or defaults to
   * {@code http://localhost:3000}. Otherwise, uses {@code BASE_URL_PROD} or defaults to
   * {@code https://serverest.dev}. Variables are loaded from the optional {@code .env} file.
   */
  @BeforeAll
  static void setupRestAssured() {
    final boolean isCi = "true".equalsIgnoreCase(System.getenv("CI"));

    final String devUrl = DOTENV.get("BASE_URL_DEV");
    final String prodUrl = DOTENV.get("BASE_URL_PROD");

    if (isCi) {
      RestAssured.baseURI = devUrl != null && !devUrl.isBlank() ? devUrl : "http://localhost:3000";
    } else {
      RestAssured.baseURI = prodUrl != null && !prodUrl.isBlank() ? prodUrl : "https://serverest.dev";
    }
  }
}
