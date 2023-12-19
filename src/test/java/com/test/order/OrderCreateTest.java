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
        userData = getRandomUser();
        token = userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        ;
        userLogin = UserCredentials.from(userData);
        orderClient = new OrderClient();
        orderData = getRandomOrder();
    }
    @After
    public void tearDown() {
        userClient.deleteUser(token)
                .assertThat()
                .statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void orderCreationWithAuth() {
        token = userClient.loginUser(userLogin)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());
    }
    @Test
    @DisplayName("Создание заказа без авторизацией")
    public void orderCreationWithoutAuth() {
        orderClient.createWithoutAuthorization(orderData)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }
    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void orderCreationWithoutIngredients() {
        token = userClient.loginUser(userLogin)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        orderData.setIngredients(null);
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @Test
    @DisplayName("Создание заказа с неправильным хешем")
    public void orderCreationWithIncorrectHash() {
        token = userClient.loginUser(userLogin)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        List<String> incorrectHash = new ArrayList<>();
        incorrectHash.add(RandomStringUtils.randomAlphabetic(10));
        orderData.setIngredients(incorrectHash);
        orderClient.createWithAuthorization(orderData, token)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}

