package com.test.order;

import com.example.client.*;
import com.example.data.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static com.example.data.UserGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;


public class OrderGetTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private UserData user;
    private UserCredentials userLogin;
    private OrderData order;
    private String token;

    @Test
    @DisplayName("Получение заказа с авторизацией")
    public void getOrdersWithAuth() {
        userClient = new UserClient();
        user = getRandomUser();
        token = userClient.createUser(user)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        ;
        userLogin = UserCredentials.from(user);
        token = userClient.loginUser(userLogin)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        orderClient = new OrderClient();
        orderClient.getWithAuthorization(token)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        userClient.deleteUser(token)
                .assertThat()
                .statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказа без авторизацией")
    public void getOrdersWithoutAuth() {
        orderClient = new OrderClient();
        orderClient.getWithoutAuthorization()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
