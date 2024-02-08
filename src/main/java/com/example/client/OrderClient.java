package com.example.client;

import com.example.data.OrderData;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;


import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient  {
    private static final String PATH_ORDER = "api/orders/";
    @Step("Создание заказа без авторизации")
    public ValidatableResponse createWithoutAuthorization(OrderData ingredients) {
        return given()
                .header("Content-type", "application/json")
                .spec(requestSpecification())
                .body(ingredients)
                .when()
                .post(PATH_ORDER)
                .then();
    }
    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createWithAuthorization(OrderData ingredients, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .spec(requestSpecification())
                .body(ingredients)
                .when()
                .post(PATH_ORDER)
                .then();
    }

    @Step("Получение заказа с авторизацией")
    public ValidatableResponse getWithAuthorization(String token) {
        return given()
                .header("Authorization", token)
                .spec(requestSpecification())
                .when()
                .get(PATH_ORDER)
                .then();
    }

    @Step("Получение заказа без авторизации")
    public ValidatableResponse getWithoutAuthorization() {
        return given()
                .header("Content-type", "application/json")
                .spec(requestSpecification())
                .when()
                .get(PATH_ORDER)
                .then();
    }
}
