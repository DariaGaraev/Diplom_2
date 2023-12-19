package com.test.user;

import com.example.data.*;
import com.example.client.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;

import static com.example.data.UserGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class UserLoginTest {
    private UserData userData;
    private UserClient userClient;
    private UserCredentials userCredentials;
    private String token;

    @Before
    @Step("Создание тестовых данных пользователя")
    public void setUp() {
        userClient = new UserClient();
        userData = getRandomUser();
        token = userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        userCredentials = UserCredentials.from(userData);
    }
    @After
    @Step("Удаление тестовых данных пользователя")
    public void cleanUp() {
        userClient.deleteUser(token)
                .assertThat()
                .statusCode(SC_ACCEPTED)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Авторизация пользователя")
    public void LoginUserTest() {
        ValidatableResponse response = userClient.loginUser(userCredentials);
        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        token = response.extract().path("accessToken").toString().substring(6).trim();
    }


    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void testLoginUserWithWrongPassword() {
        userCredentials.setPassword("BlaBlaLogin123123");
        ValidatableResponse response = userClient.loginUser(userCredentials);;
        response.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false),"message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверной почтой")
    public void testLoginUserWithWrongEmail() {
        userCredentials.setEmail("BlaBlaEmail123123");
        ValidatableResponse response = userClient.loginUser(userCredentials);;
        response.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false),"message", equalTo("email or password are incorrect"));
    }

}
