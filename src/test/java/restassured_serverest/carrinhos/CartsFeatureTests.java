package restassured_serverest.carrinhos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import restassured_serverest.BaseApiTest;
import restassured_serverest.utils.FakerUtils;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class CartsFeatureTests extends BaseApiTest {

    private Response loginWithDefaultPayload() {
        // Cria um usuário administrador único para o teste e realiza o login
        final String userEmail = FakerUtils.randomEmail();
        final String userPassword = "SenhaSegura@123";

        final String newUser = bodyEmailAndPassword(userEmail, userPassword);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(newUser)
                .when()
                .post()
                .then()
                .statusCode(201);

        final String loginPayload = "{" +
                "\"email\":\"" + userEmail + "\"," +
                "\"password\":\"" + userPassword + "\"" +
                "}";

        return givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath("/login")
                .body(loginPayload)
                .when()
                .post();
    }

    private String createAdminUserAndGetToken() {
        final String userEmail = FakerUtils.randomEmail();
        final String userPassword = "SenhaSegura@123";

        final String newUser = "{"
                + "\"nome\":\"Cart User\","
                + "\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"administrador\":\"true\""
                + "}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(newUser)
                .when()
                .post()
                .then()
                .statusCode(201);

        final String loginPayload = "{"
                + "\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\""
                + "}";

        final Response loginResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath("/login")
                .body(loginPayload)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();

        return loginResponse.path("authorization");
    }

    private String createProduct(final String token, final int price, final int quantity, final String description) {
        final String productName = FakerUtils.randomProduct();

        final String productData = "{"
                + "\"nome\":\"" + productName + "\","
                + "\"preco\":" + price + ","
                + "\"descricao\":\"" + description + "\","
                + "\"quantidade\":" + quantity
                + "}";

        final Response productResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_PRODUTOS)
                .body(productData)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .extract().response();

        return productResponse.path(KEY_ID);
    }

    @Test
    @DisplayName("CT01 - Ciclo de vida completo do carrinho para usuário autenticado")
    void fullCartLifecycleForAuthenticatedUser() {
        final String token = createAdminUserAndGetToken();

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        final String productId = createProduct(token, 150, 10, "Product created for cart lifecycle test");

        final String cartBody = "{\"produtos\":[{\"idProduto\":\"" + productId + "\",\"quantidade\":2}]}";

        final Response createCartResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(cartBody)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String cartId = createCartResponse.path(KEY_ID);

        final Response getCartResponse = givenWithAllure()
                .basePath(ROTA_CARRINHOS + "/" + cartId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(getCartResponse.path("produtos.size()"), equalTo(1));
        assertThat(getCartResponse.path("precoTotal"), notNullValue());
        assertThat(getCartResponse.path("quantidadeTotal"), notNullValue());
        assertThat(getCartResponse.path("idUsuario"), notNullValue());
        assertThat(getCartResponse.path(KEY_ID), equalTo(cartId));

        final Response concludeResponse = givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath("/carrinhos/concluir-compra")
                .when()
                .delete()
                .then()
                .statusCode(200)
                .extract().response();

        final String message = concludeResponse.path(KEY_MESSAGE);
        assertThat(message, containsString("Registro excluído com sucesso"));
    }

    @Test
    @DisplayName("CT02 - Cancelar compra e retornar produtos ao estoque")
    void cancelPurchaseAndReturnProductsToStock() {
        final Response loginResponse = loginWithDefaultPayload()
                .then()
                .statusCode(200)
                .extract().response();

        final String token = loginResponse.path("authorization");

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        final String productId = createProduct(token, 200, 5, "Product for cancel purchase test");

        final String cartBody = "{\"produtos\":[{\"idProduto\":\"" + productId + "\",\"quantidade\":1}]}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(cartBody)
                .when()
                .post()
                .then()
                .statusCode(201);

        final Response cancelResponse = givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(cancelResponse.path(KEY_MESSAGE), notNullValue());
    }

    @Test
    @DisplayName("CT03 - Impedir criação de carrinho sem token de autenticação")
    void preventCreatingCartWithoutAuthenticationToken() {
        final String cartBody = "{\"produtos\":[{\"idProduto\":\"BeeJh5lz3k6kSIzA\",\"quantidade\":1}]}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_CARRINHOS)
                .body(cartBody)
                .when()
                .post()
                .then()
                .statusCode(401)
                .body(KEY_MESSAGE,
                        equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @DisplayName("CT04 - Impedir criação de mais de um carrinho para o mesmo usuário")
    void preventCreatingMoreThanOneCartForSameUser() {
        final Response loginResponse = loginWithDefaultPayload()
                .then()
                .statusCode(200)
                .extract().response();

        final String token = loginResponse.path("authorization");

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        final String productId = createProduct(token, 120, 3, "Product for multiple cart test");

        final String firstCart = "{\"produtos\":[{\"idProduto\":\"" + productId + "\",\"quantidade\":1}]}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(firstCart)
                .when()
                .post()
                .then()
                .statusCode(201);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(firstCart)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, containsString("Não é permitido ter mais de 1 carrinho"));
    }

    @Test
    @DisplayName("CT05 - Carrinho não encontrado por ID")
    void cartNotFoundById() {
        givenWithAllure()
                .basePath(ROTA_CARRINHOS + "/invalid-cart-id-123")
                .when()
                .get()
                .then()
                .statusCode(400)
                .body("id", equalTo("id deve ter exatamente 16 caracteres alfanuméricos"));
    }

    @Test
    @DisplayName("CT06 - Impedir criação de carrinho quando o estoque do produto for insuficiente")
    void preventCartCreationWhenProductStockIsInsufficient() {
        final Response loginResponse = loginWithDefaultPayload()
                .then()
                .statusCode(200)
                .extract().response();

        final String token = loginResponse.path("authorization");

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        final String productId = createProduct(token, 100, 1, "Low stock product for cart test");

        final String cartBody = "{\"produtos\":[{\"idProduto\":\"" + productId + "\",\"quantidade\":2}]}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(cartBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, containsString("Produto não possui quantidade suficiente"));
    }

    @Test
    @DisplayName("CT07 - Impedir criação de carrinho com produtos duplicados no mesmo carrinho")
    void preventCartCreationWithDuplicatedProductsInSameCart() {
        final Response loginResponse = loginWithDefaultPayload()
                .then()
                .statusCode(200)
                .extract().response();

        final String token = loginResponse.path("authorization");

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        final String productId = createProduct(token, 150, 10, "Product created for duplicated products cart test");

        final String duplicatedCartBody = "{\"produtos\":[{\"idProduto\":\"" + productId
                + "\",\"quantidade\":1},{\"idProduto\":\"" + productId + "\",\"quantidade\":1}]}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .basePath(ROTA_CARRINHOS)
                .body(duplicatedCartBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, containsString("Não é permitido possuir produto duplicado"));
    }

}
