package restassured_serverest;

import org.junit.jupiter.api.BeforeAll;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import restassured_serverest.utils.FakerUtils;

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

    static Dotenv dotenv = Dotenv.configure()
        .directory("./.env")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();

    protected RequestSpecification givenWithAllure() {
        return RestAssured.given().filter(new AllureRestAssured());
    }

    protected String bodyPayload(String name, String email, String  password, String administrador) {
        return "{\n" +
                "  \"nome\": \"" + name + "\",\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"administrador\": \"" + administrador + "\"" +
                "}";
    }

    protected String bodyEmail(String email) {
        final String name = FakerUtils.randomName();
        final String password = FakerUtils.randomPassword();

        return "{\n  \"nome\": \""+ name + "\",\n  \"email\": \""+ email + "\",\n  " +
                "\"password\": \"" + password + "\",\n  \"administrador\": \"false\"\n}";
    }

    protected String bodyEmailAndPassword(String email, String password) {

        return "{" +
                "\"nome\":\"Cart Default User\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"administrador\":\"true\"" +
                "}";
    }

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.baseURI = dotenv.get("BASE_URL");
    }
}
