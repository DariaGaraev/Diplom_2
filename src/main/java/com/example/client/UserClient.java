package com.example.client;

import com.example.data.*;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{
    private static final String REGISTER_PATH = "api/auth/register";
    private static final String LOGIN_PATH = "api/auth/login";
    private static final String USER_PATH = "api/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(UserData user) {
        return given()
                .header("Content-type", "application/json")
                .spec(requestSpecification())
                .body(user)
                .post(REGISTER_PATH)
                .then().log().all();
    }
    @Step("Вход в аккаунт")
    public ValidatableResponse loginUser(UserCredentials userCredential) {
        return given()
                .header("Content-type", "application/json")
                .spec(requestSpecification())
                .body(userCredential)
                .post(LOGIN_PATH)
                .then();
    }
    @Step("Изменение данных пользователя с авторизацией")
    //change
    public ValidatableResponse changeUserWithAuthorization(UserData user, String token) {
        return given()
                .header("Authorization", token)
                .spec(requestSpecification())
                .body(user)
                .patch(USER_PATH)
                .then();
    }
    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse changeUserWithoutAuthorization(UserData user) {
        return given()
                .spec(requestSpecification())
                .body(user)
                .patch(USER_PATH)
                .then();
    }
    @Step("Удаление данных")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .spec(requestSpecification())
                .delete(USER_PATH)
                .then();
    }
}
