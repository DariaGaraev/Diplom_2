package com.example.client;

import com.example.data.*;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{
    private static final String REGISTER_PATH = "api/auth/register";
    private static final String LOGIN_PATH = "api/auth/login";
    private static final String USER_PATH = "api/auth/user";
    @Step("Создать пользователя")
    public ValidatableResponse createUser(UserData user) {
        return given()
                .spec(requestSpecification())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }
    @Step("Войти в аккаунт")
    public ValidatableResponse loginUser(UserCredentials userCredential) {
        return given()
                .spec(requestSpecification())
                .body(userCredential)
                .when()
                .post(LOGIN_PATH)
                .then();
    }
    @Step("Изменить данные пользователя с авторизацией")
    //change
    public ValidatableResponse changeUserWithAuthorization(UserData user, String token) {
        return given()
                .header("Authorization", token)
                .spec(requestSpecification())
                .body(user)
                .patch(USER_PATH)
                .then();
    }
    @Step("Изменить данные пользователя без авторизацией")
    public ValidatableResponse changeUserWithoutAuthorization(UserData user) {
        return given()
                .spec(requestSpecification())
                .body(user)
                .when()
                .patch(USER_PATH)
                .then();
    }
    @Step("удалить данные")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .spec(requestSpecification())
                .when()
                .delete(USER_PATH)
                .then();
    }
}
