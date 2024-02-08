package com.test.order;


import com.example.client.*;
import com.example.data.*;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.data.OrderGenerator.getRandomOrder;
import static com.example.data.UserGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class OrderCreateTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private UserData userData;
    private UserCredentials userLogin;
    private OrderData orderData;
    private String token;
    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        userData = getRandomUser();
        orderData = getRandomOrder();
        userClient.createUser(userData);
        userLogin = UserCredentials.from(userData);
    }
    @After
    public void tearDown() {
        if (token != null) {
            userClient.deleteUser(token);
        }
    }
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void orderCreationWithAuth() {
        token = userClient.loginUser(userLogin).extract().path("accessToken");
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true),"order.number", notNullValue());
    }
    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreationWithoutAuth() {
        orderClient.createWithoutAuthorization(orderData)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true),"order.number", notNullValue());

    }
    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void orderCreationWithoutIngredients() {
        token = userClient.loginUser(userLogin).extract().path("accessToken");
        orderData.setIngredients(null);
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false),"message", equalTo("Ingredient ids must be provided"));
    }
    @Test
    @DisplayName("Создание заказа с неправильным хешем")
    public void orderCreationWithIncorrectHash() {
        token = userClient.loginUser(userLogin).extract().path("accessToken");
        List<String> incorrectHash = new ArrayList<>();
        incorrectHash.add(RandomStringUtils.randomAlphabetic(10));
        orderData.setIngredients(incorrectHash);
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}

