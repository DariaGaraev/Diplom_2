package com.test.order;

import com.example.client.*;
import com.example.data.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.example.data.UserGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;


public class OrderGetTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private UserData userData;
    private String token;
    @Before
    @Step("Создание тестовых данных")
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        userData = getRandomUser();
        token = userClient.createUser(userData).extract().path("accessToken");
    }
    @After
    @Step("Удаление тестовых данных")
    public void cleanUp() {
        if(token!=null) {
            userClient.deleteUser(token);
        }
    }
    @Test
    @DisplayName("Получение заказа с авторизацией")
    public void getOrdersWithAuth() {
        orderClient.getWithAuthorization(token)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Получение заказа без авторизации")
    public void getOrdersWithoutAuth() {
        orderClient.getWithoutAuthorization()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false),"message", equalTo("You should be authorised"));
    }
}
