package restassured_serverest;

import org.junit.jupiter.api.BeforeAll;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

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
    public static final String KEY_ADMINISTRADOR = "administrador";

    static Dotenv dotenv = Dotenv.configure()
        .directory("./.env")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();

    protected RequestSpecification givenWithAllure() {
        return RestAssured.given().filter(new AllureRestAssured());
    }

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.baseURI = dotenv.get("BASE_URL");
    }
}
