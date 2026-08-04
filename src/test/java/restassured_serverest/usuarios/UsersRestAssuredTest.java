package restassured_serverest.usuarios;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class UsersRestAssuredTest extends BaseApiTest {

    @Test
    @DisplayName("CT01 - Listar todos os usuários e validar estrutura JSON")
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
        assertThat(usuarios, everyItem(hasKey("password")));
        assertThat(usuarios, everyItem(hasKey(KEY_ADMINISTRADOR)));
        assertThat(usuarios, everyItem(hasKey(KEY_ID)));

        final List<String> emails = usuarios.stream()
                .map(u -> String.valueOf(u.get(KEY_EMAIL)))
                .collect(Collectors.toList());

        assertThat(emails, everyItem(matchesPattern(".+@.+\\..+")));
    }

    @Test
    @DisplayName("CT02 - Buscar usuário específico por ID")
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
    @DisplayName("CT03 - Criar um novo usuário com validações completas")
    void createUser() {
        final String email = FakerUtils.randomEmail();
        final String name = FakerUtils.randomName();
        final String password = FakerUtils.randomPassword();

        final String payload = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"true\"\n}",
                name, email, password);

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(payload)
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
                .body(KEY_EMAIL, equalTo(email));
    }

    @Test
    @DisplayName("CT05 - Validar mensagens de erro ao criar e-mail duplicado")
    void duplicateEmailValidation() {
        final String duplicateEmail = FakerUtils.randomEmail();

        final String user1 = "{\n  \"nome\": \"User 1\",\n  \"email\": \"" + duplicateEmail
                + "\",\n  \"password\": \"senha123\",\n  \"administrador\": \"false\"\n}";

        givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(user1)
                .when()
                .post()
                .then()
                .statusCode(201);

        final String user2 = "{\n  \"nome\": \"User 2\",\n  \"email\": \"" + duplicateEmail
                + "\",\n  \"password\": \"anotherpassword\",\n  \"administrador\": \"true\"\n}";

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
    @DisplayName("CT04 - Validações JSON avançadas com filtros")
    void advancedJsonValidationsWithFilters() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final List<Map<String, Object>> usuarios = response.path("usuarios");

        final List<Map<String, Object>> admins = usuarios.stream()
                .filter(u -> "true".equals(String.valueOf(u.get(KEY_ADMINISTRADOR))))
                .collect(Collectors.toList());

        assertThat(admins.size(), greaterThan(0));

        final List<Map<String, Object>> filteredUsers = response.jsonPath()
                .getList("usuarios.findAll { it.administrador == 'true' }");

        assertThat(filteredUsers.size(), greaterThan(0));

        final List<String> emails = usuarios.stream()
                .map(u -> String.valueOf(u.get(KEY_EMAIL)))
                .collect(Collectors.toList());

        assertThat(emails, everyItem(notNullValue()));
    }

    @Test
    @DisplayName("CT06 - Validar busca por parâmetro administrador")
    void validateWithFuzzyMatching() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .param(KEY_ADMINISTRADOR, "true")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final int quantidade = response.path("quantidade");
        final List<Map<String, Object>> usuarios = response.path("usuarios");

        assertThat(quantidade, greaterThanOrEqualTo(0));

        for (final Map<String, Object> user : usuarios) {
            assertThat(user.get("nome"), notNullValue());
            assertThat(user.get("email"), notNullValue());
            assertThat(String.valueOf(user.get("administrador")), equalTo("true"));
        }
    }

    @Test
    @DisplayName("CT07 - Validações condicionais baseadas em valores")
    void conditionalValidationsBasedOnValues() {
        final Response response = givenWithAllure()
                .basePath(ROTA_USUARIOS)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        final Map<String, Object> user = response.path("usuarios[0]");

        final String adminFlag = String.valueOf(user.get(KEY_ADMINISTRADOR));
        assertTrue("true".equals(adminFlag) || "false".equals(adminFlag));

        final String email = String.valueOf(user.get(KEY_EMAIL));
        final String password = String.valueOf(user.get("password"));

        assertTrue(email != null && email.length() > 5);
        assertTrue(password != null && password.length() > 0);
    }

    @Test
    @DisplayName("CT08 - Validar formatos com expressões regulares")
    void validateFormatsWithRegularExpressions() {
        final String newEmail = "test.regex." + System.currentTimeMillis() + "@example.com";

        final String userData = String.format(
                "{\n  \"nome\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"administrador\": \"false\"\n}",
                "Regex Test", newEmail, "StrongPassword@123");

        final Response createResponse = givenWithAllure()
                .contentType(ContentType.JSON)
                .basePath(ROTA_USUARIOS)
                .body(userData)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        final String userId = createResponse.path(KEY_ID);

        givenWithAllure()
                .basePath(ROTA_USUARIOS + "/" + userId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("email", matchesPattern(".+@.+\\..+"))
                .body("nome", matchesPattern("[A-Za-z\\s]+"))
                .body("_id", matchesPattern("[A-Za-z0-9]+"));
    }

    @Test
    @DisplayName("CT09 - Validar ausência de campos não mapeados")
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
    @DisplayName("CT11 - Preparar dados para validação de cadastro")
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
    @DisplayName("CT14 - Impedir exclusão de usuário que possui carrinho associado")
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
    @DisplayName("CT15 - Buscar usuário por ID inválido deve retornar 400")
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
    @DisplayName("CT16 - Impedir atualização de usuário com e-mail duplicado")
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

}
