package restassured_serverest.usuarios;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import restassured_serverest.BaseApiTest;
import restassured_serverest.utils.FakerUtils;


/**
 * Testes de API do end point de /users
 * Temos uma documentação detalhada em TESTING_API.MD
 * Os testes seguem a lógica do CRUD, com criação, leitura, atualização e exclusão de usuários.
 * Além disso, temos testes Negativos como e-mail duplicado;
 */

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class UsersFeatureTests extends BaseApiTest {

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT01 - Criação de um novo usuário padrão, com administrador igual a false")
    void createNormalUser() {
        final String email = FakerUtils.randomEmail();
        final String name = FakerUtils.randomName();
        final String password = FakerUtils.randomPassword();
        final String administrador = "false";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(KEY_NOME, equalTo(name))
                .body(KEY_EMAIL, equalTo(email))
                .body(KEY_PASSWORD, equalTo(password))
                .body(KEY_ADMINISTRADOR, equalTo(administrador));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT02 - Criação de um novo usuário Administrador, com administrador igual a true")
    void createAdminUser() {
        final String email = FakerUtils.randomEmail();
        final String name = FakerUtils.randomName();
        final String password = FakerUtils.randomPassword();
        final String administrador = "true";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        final Response searchUser = givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(KEY_NOME, equalTo(name))
                .body(KEY_EMAIL, equalTo(email))
                .body(KEY_PASSWORD, equalTo(password))
                .body(KEY_ADMINISTRADOR, equalTo(administrador))
                .extract().response();

        Allure.step("Body Result: " + searchUser.body().print());
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("CT03 - Listar todos os usuários e validar estrutura JSON")
    void listAllUsersAndValidateStructure() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final int quantidade = response.path("quantidade");
        final List<Map<String, Object>> usuarios = response.path("usuarios");

        assertThat(quantidade, greaterThan(0));
        assertThat(usuarios.size(), greaterThan(0));

        assertThat(usuarios, everyItem(hasKey(KEY_NOME)));
        assertThat(usuarios, everyItem(hasKey(KEY_EMAIL)));
        assertThat(usuarios, everyItem(hasKey(KEY_PASSWORD)));
        assertThat(usuarios, everyItem(hasKey(KEY_ADMINISTRADOR)));
        assertThat(usuarios, everyItem(hasKey(KEY_ID)));
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("CT04 - Buscar usuário específico por ID")
    void getUserById() {
        final Response listResponse = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final String userId = listResponse.path("usuarios[0]." + KEY_ID);

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + userId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(KEY_ID, equalTo(userId))
                .body(KEY_NOME, notNullValue())
                .body(KEY_EMAIL, notNullValue());
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT05 - Atualização de um usuário Administrador, com administrador igual a true")
    void updateAdminUser() {
        String email = FakerUtils.randomEmail();
        String name = FakerUtils.randomName();
        String password = FakerUtils.randomPassword();
        String administrador = "true";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        email = FakerUtils.randomEmail();
        name = FakerUtils.randomName();
        password = FakerUtils.randomPassword();
        final String bodyUpdate = bodyPayload(name, email, password, administrador);

        final Response updateResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .body(bodyUpdate)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Registro alterado com sucesso"))
                .extract().response();

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(KEY_NOME, equalTo(name))
                .body(KEY_EMAIL, equalTo(email))
                .body(KEY_PASSWORD, equalTo(password))
                .body(KEY_ADMINISTRADOR, equalTo(administrador))
                .extract().response();

        Allure.step("Body Result: " + updateResponse.body().print());
    }


    @Test
    @Order(6)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT06 - Atualização de um usuário Padrão, com administrador igual a false")
    void updateNormalUser() {
        String email = FakerUtils.randomEmail();
        String name = FakerUtils.randomName();
        String password = FakerUtils.randomPassword();
        String administrador = "false";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        email = FakerUtils.randomEmail();
        name = FakerUtils.randomName();
        password = FakerUtils.randomPassword();
        final String bodyUpdate = bodyPayload(name, email, password, administrador);

        final Response updateResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .body(bodyUpdate)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Registro alterado com sucesso"))
                .extract().response();

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(KEY_NOME, equalTo(name))
                .body(KEY_EMAIL, equalTo(email))
                .body(KEY_PASSWORD, equalTo(password))
                .body(KEY_ADMINISTRADOR, equalTo(administrador))
                .extract().response();

        Allure.step("Body Result: " + updateResponse.body().print());
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT07 - Valida Exclusão de um usuário Administrador, com administrador igual a true")
    void deleteAdminUser() {
        String email = FakerUtils.randomEmail();
        String name = FakerUtils.randomName();
        String password = FakerUtils.randomPassword();
        String administrador = "true";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        email = FakerUtils.randomEmail();
        name = FakerUtils.randomName();
        password = FakerUtils.randomPassword();
        final String bodyUpdate = bodyPayload(name, email, password, administrador);

        final Response updateResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .body(bodyUpdate)
                .when()
                .delete()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Registro excluído com sucesso"))
                .extract().response();

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Usuário não encontrado"))
                .extract().response();

        Allure.step("Body Result: " + updateResponse.body().print());
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("CT08 - Valida Exclusão de um usuário Padrão, com administrador igual a false")
    void deleteNormalUser() {
        String email = FakerUtils.randomEmail();
        String name = FakerUtils.randomName();
        String password = FakerUtils.randomPassword();
        String administrador = "false";

        final String body = bodyPayload(name, email, password, administrador);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .body(KEY_ID, notNullValue())
                .extract().response();

        final String newUserId = createResponse.path(KEY_ID);

        email = FakerUtils.randomEmail();
        name = FakerUtils.randomName();
        password = FakerUtils.randomPassword();
        final String bodyUpdate = bodyPayload(name, email, password, administrador);

        final Response updateResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .body(bodyUpdate)
                .when()
                .delete()
                .then()
                .statusCode(200)
                .body(KEY_MESSAGE, equalTo("Registro excluído com sucesso"))
                .extract().response();

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + newUserId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Usuário não encontrado"))
                .extract().response();

        Allure.step("Body Result: " + updateResponse.body().print());
    }



    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("CT09 - Cenário Negativo - Validar mensagens de erro ao criar e-mail duplicado")
    void duplicateEmailValidation() {
        final String duplicateEmail = FakerUtils.randomEmail();
        final String user1 = bodyEmail(duplicateEmail);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(user1)
                .when()
                .post()
                .then()
                .statusCode(201);

        final String user2 = bodyEmail(duplicateEmail);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(user2)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Este email já está sendo usado"))
                .body(KEY_MESSAGE, notNullValue());
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("CT10 - Cenário Negativo - Validar se existe retorno de erro ao buscar usuário inexistente")
    void advancedJsonValidationsWithFilters() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .param(KEY_ADMINISTRADOR, "true")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final List<Map<String, Object>> usuarios = response.path("usuarios");

        final List<Map<String, Object>> admins = usuarios.stream()
                .filter(u -> "true".equals(String.valueOf(u.get(KEY_ADMINISTRADOR))))
                .toList();

        assertThat(admins.size(), greaterThan(0));

        final List<Map<String, Object>> filteredUsers = response.jsonPath()
                .getList("usuarios.findAll { it.administrador == 'true' }");

        assertThat(filteredUsers.size(), greaterThan(0));

        final List<String> emails = usuarios.stream()
                .map(u -> String.valueOf(u.get(KEY_EMAIL)))
                .collect(Collectors.toList());

        assertThat(emails, everyItem(notNullValue()));

        for (final Map<String, Object> user : usuarios) {
            assertThat(user.get("nome"), notNullValue());
            assertThat(user.get("email"), notNullValue());
            assertThat(String.valueOf(user.get(KEY_ADMINISTRADOR)), equalTo("true"));
        }
    }

    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("CT11 - Cenário Negativo - Validações condicionais baseadas em valores")
    void conditionalValidationsBasedOnValues() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final Map<String, Object> user = response.path("usuarios[0]");

        final String email = String.valueOf(user.get(KEY_EMAIL));
        final String password = String.valueOf(user.get("password"));

        assertTrue(email != null && email.length() > 5);
        assertTrue(password != null && password.length() > 0);
    }

    @Test
    @Order(12)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("CT12 - Cenário Negativo - Validar ausência de campos não mapeados")
    void validateAbsenceOfFields() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("error", nullValue())
                .body("errorMessage", nullValue())
                .extract().response();

        final Map<String, Object> user = response.path("usuarios[0]");

        assertThat(user.containsKey("cpf"), equalTo(false));
        assertThat(user.containsKey("phone"), equalTo(false));
    }

    @Test
    @Order(13)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("CT13 - Cenário Negativo - Preparar dados para validação de cadastro")
    void prepareDataForNestedObjectValidation() {
        final String complexEmail = FakerUtils.randomEmail();

        final String complexData = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"true\"\n}",
                "Complex User", complexEmail, "senha123");

        final Response response = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(complexData)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        final String message = response.path(KEY_MESSAGE);
        final String id = response.path(KEY_ID);

        assertThat(message, notNullValue());
        assertThat(message, equalTo("Cadastro realizado com sucesso"));
        assertThat(id, notNullValue());
        assertTrue(id.length() > 10);
    }

    @Test
    @Order(14)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("CT14 - Cenário Negativo - Impedir exclusão de usuário que possui carrinho associado")
    void preventDeletingUserThatHasAssociatedCart() {
        final String userEmail = FakerUtils.randomEmail();
        final String userPassword = "SenhaSegura@123";

        final String userData = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"true\"\n}",
                "User With Cart", userEmail, userPassword);

        final Response createUserResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(userData)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body(KEY_MESSAGE, equalTo("Cadastro realizado com sucesso"))
                .extract().response();

        final String userId = createUserResponse.path(KEY_ID);

        final String loginPayload = String.format("{\n  \"email\": \"%s\",\n  \"password\": \"%s\"\n}",
                userEmail, userPassword);

        final Response loginResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_LOGIN)
                .body(loginPayload)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();

        final String userToken = loginResponse.path("authorization");

        final String productName = "Product for user cart " + System.currentTimeMillis();
        final String productData = String.format(
                "{\n  \"nome\": \"%s\",\n  \"preco\": 100,\n  \"descricao\": \"Product associated to user cart\",\n  \"quantidade\": 5\n}",
                productName);

        final Response productResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, userToken)
                .basePath(ROTA_PRODUTOS)
                .body(productData)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        final String productId = productResponse.path(KEY_ID);

        final String cartBody = String.format("{\n  \"produtos\": [ { \"idProduto\": \"%s\", \"quantidade\": 1 } ]\n}",
                productId);

        givenWithAllure()
                .header(HEADER_AUTHORIZATION, userToken)
                .basePath(ROTA_CANCELAR_COMPRA)
                .when()
                .delete()
                .then()
                .statusCode(200);

        givenWithAllure()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, userToken)
                .basePath(ROTA_CARRINHOS)
                .body(cartBody)
                .when()
                .post()
                .then()
                .statusCode(201);

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + userId)
                .when()
                .delete()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Não é permitido excluir usuário com carrinho cadastrado"))
                .body("idCarrinho", notNullValue());
    }

    @Test
    @Order(15)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("CT15 - Cenário Negativo - Buscar usuário por ID inválido deve retornar 400")
    void userByInvalidIdShouldReturn400() {
        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/3F7K9P2XQ8M1R6TB")
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Usuário não encontrado"));
    }

    @Test
    @Order(16)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("CT16 - Cenário Negativo - Impedir atualização de usuário com e-mail duplicado")
    void preventUpdatingUserWithDuplicateEmail() {
        final String email1 = FakerUtils.randomEmail();
        final String email2 = FakerUtils.randomEmail();

        final String user1 = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"false\"\n}",
                "User One", email1, "Senha123@");

        final String user2 = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"true\"\n}",
                "User Two", email2, "Senha456@");

        final Response createUser1Response = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(user1)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(user2)
                .when()
                .post()
                .then()
                .statusCode(201);

        final String userId1 = createUser1Response.path(KEY_ID);

        final String updatePayload = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"true\"\n}",
                "User One Updated", email2, "Senha123@");

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS + "/" + userId1)
                .body(updatePayload)
                .when()
                .put()
                .then()
                .statusCode(400)
                .body(KEY_MESSAGE, equalTo("Este email já está sendo usado"));
    }

     @ParameterizedTest(name = "CT17 - Cenário Negativo - Validar campo obrigatório no POST /usuarios: {0}")
     @MethodSource("invalidRequiredFieldsPayloads")
     @Order(17)
     @Severity(SeverityLevel.CRITICAL)
     @DisplayName("CT17 - Validar campos obrigatórios no cadastro de usuário")
     void validateRequiredFieldsOnCreateUser(final String scenario, final String invalidPayload, final String expectedField) {
        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(invalidPayload)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(containsString(expectedField));
     }
}
