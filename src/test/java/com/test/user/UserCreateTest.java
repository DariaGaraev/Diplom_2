package com.test.user;

import com.example.client.UserClient;
import com.example.data.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
public class UserCreateTest {
    private UserClient userClient;
    private UserData userData;
    private String token;
    @Before
    @Step("Создание тестовых данных")
    public void setUp() {
        userClient = new UserClient();
        userData = UserGenerator.getRandomUser();
        token = null;
    }
    @After
    @Step("Удаление тестовых данных")
    public void tearDown() {
        if (token != null) {
            userClient.deleteUser(token);
        }
    }
    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        ValidatableResponse response = userClient.createUser(userData);
        response.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true)).log().all();
        token = response.extract().path("accessToken").toString().substring(6).trim();
    }
    @Test
    @DisplayName("Создание неуникального пользователя")
    public void identicalLoginTest() {

        token = userClient.createUser(userData).extract().path("accessToken").toString().substring(6).trim();

        userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Создание пользователя без имени")
    public void emptyNameTest() {
        userData.setName(null);
        userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));

    }
    @Test
    @DisplayName("Создание пользователя без e-mail")
    public void emptyEmailTest() {
        userData.setEmail(null);
        userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без пароля")
    public void emptyPasswordTest() {
        userData.setPassword(null);
        userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
