package com.test.user;

import com.example.client.UserClient;
import com.example.data.*;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import static org.apache.http.HttpStatus.*;
import static com.example.data.UserGenerator.getRandomUser;
import static org.hamcrest.CoreMatchers.equalTo;
public class UserCreateTest {
    private UserClient userClient;
    private UserData userData;
    private UserCredentials userCredentials;
    private String token;
    @Before
    public void setUp() {
        userClient = new UserClient();
        userData = UserGenerator.getRandomUser();
        token = null;
    }
    @After
    public void tearDown() {
        if (token != null) {
            userClient.deleteUser(token)
                    .assertThat()
                    .statusCode(SC_ACCEPTED)
                    .body("success", equalTo(true));
        }
    }
    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {

        userCredentials = UserCredentials.from(userData);
        userClient.loginUser(userCredentials)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true)).log().all();
    }
    @Test
    @DisplayName("Создание не уникального пользователя")
    public void identicalLoginTest() {
        token = userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        userData.setPassword(RandomStringUtils.randomAlphabetic(9));
        userData.setName(RandomStringUtils.randomAlphabetic(9));
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
    @DisplayName("Создание пользователя без email")
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
