package restassured_serverest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;
import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import restassured_serverest.utils.FakerUtils;
import java.util.Map;
import java.util.stream.Stream;

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

    private static final Dotenv DOTENV = Dotenv.configure()
        .directory("./.env")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();

    public static Stream<Arguments> invalidRequiredFieldsPayloads() {
            return Stream.of(
                        Arguments.of("nome ausente", "{\n  \"email\": \"required.nome.absent@test.com\",\n  \"password\": \"Senha123@\",\n  \"administrador\": \"false\"\n}", "nome"),
                        Arguments.of("email ausente", "{\n  \"nome\": \"Usuário sem email\",\n  \"password\": \"Senha123@\",\n  \"administrador\": \"false\"\n}", "email"),
                        Arguments.of("password ausente", "{\n  \"nome\": \"Usuário sem password\",\n  \"email\": \"required.password.absent@test.com\",\n  \"administrador\": \"false\"\n}", "password"),
                        Arguments.of("administrador ausente", "{\n  \"nome\": \"Usuário sem admin\",\n  \"email\": \"required.admin.absent@test.com\",\n  \"password\": \"Senha123@\"\n}", "administrador"),
                        Arguments.of("nome vazio", "{\n  \"nome\": \"\",\n  \"email\": \"required.nome.empty@test.com\",\n  \"password\": \"Senha123@\",\n  \"administrador\": \"false\"\n}", "nome"),
                        Arguments.of("email vazio", "{\n  \"nome\": \"Usuário email vazio\",\n  \"email\": \"\",\n  \"password\": \"Senha123@\",\n  \"administrador\": \"false\"\n}", "email"),
                        Arguments.of("password vazio", "{\n  \"nome\": \"Usuário password vazio\",\n  \"email\": \"required.password.empty@test.com\",\n  \"password\": \"\",\n  \"administrador\": \"false\"\n}", "password"),
                        Arguments.of("administrador vazio", "{\n  \"nome\": \"Usuário admin vazio\",\n  \"email\": \"required.admin.empty@test.com\",\n  \"password\": \"Senha123@\",\n  \"administrador\": \"\"\n}", "administrador"));
    }

    protected RequestSpecification givenWithAllure() {
        return RestAssured.given().filter(new AllureRestAssured());
    }

    /**
     * Monta o payload padrão de criação/atualização de usuário.
     *
     * @param name nome do usuário
     * @param email e-mail do usuário
     * @param password senha do usuário
     * @param administrador flag de administrador ({@code "true"} ou {@code "false"})
     * @return payload no formato esperado pela API de usuários
     */
    protected Map<String, Object> bodyPayload(final String name, final String email, final String  password, final String administrador) {
        return Map.of(
                KEY_NOME, name,
                KEY_EMAIL, email,
                KEY_PASSWORD, password,
                KEY_ADMINISTRADOR, administrador);
    }

    /**
     * Monta um payload de usuário com e-mail informado e dados aleatórios para nome/senha.
     *
     * @param email e-mail a ser utilizado no payload
     * @return payload de usuário com {@code administrador = "false"}
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
     * Monta um payload de usuário com nome fixo para cenários de autenticação/carrinho.
     *
     * @param email e-mail do usuário
     * @param password senha do usuário
     * @return payload de usuário com {@code administrador = "true"}
     */
    protected Map<String, Object> bodyEmailAndPassword(final String email, final String password) {
        return Map.of(
                KEY_NOME, "Cart Default User",
                KEY_EMAIL, email,
                KEY_PASSWORD, password,
                KEY_ADMINISTRADOR, "true");
    }

    /**
     * Configura a {@code baseURI} global do RestAssured antes da execução dos testes.
     * <p>
     * Regras aplicadas:
     * <ul>
     *   <li>Quando {@code CI=true}, prioriza {@code BASE_URL_DEV}; se ausente/vazia, usa {@code http://localhost:3000}.</li>
     *   <li>Fora de CI, prioriza {@code BASE_URL_PROD}; se ausente/vazia, usa {@code https://serverest.dev}.</li>
     * </ul>
     * As variáveis são carregadas via {@code dotenv} (arquivo {@code .env}, quando presente).
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
