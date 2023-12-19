package com.test.user;

import com.example.client.UserClient;
import com.example.data.UserCredentials;
import com.example.data.UserData;
import io.qameta.allure.junit4.DisplayName;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.example.data.UserGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(Parameterized.class)
public class UserChangeTest {
    private UserClient userClient;
    private UserData userData;
    private UserCredentials userCredentials;
    private String token;

    private final String userName;
    private final String userEmail;
    private final String userPassword;

    public UserChangeTest(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Parameterized.Parameters(name = "userName={0}, userEmail = {1}, userPassword={2}")
    public static Object[][] params() {
        UserData userData = getRandomUser();
        return new Object[][]{
                {userData.getName(), null, null},
                {null, userData.getEmail(), null},
                {null, null, userData.getPassword()},
                {userData.getName(), userData.getEmail(), userData.getPassword()}
        };
    }
    @Before
    public void setUp() {
        userClient = new UserClient();
        userData = getRandomUser();
        token = userClient.createUser(userData)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }
    @After
    public void tearDown() {
        userClient.deleteUser(token)
                .assertThat()
                .statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Изиенение данных пользователя с авторизацией")
    public void userWithAuthTest() {
        userCredentials = UserCredentials.from(userData);
        token = userClient.loginUser(userCredentials)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        if (userName != null) userData.setName(userName);
        if (userEmail != null) userData.setEmail(userEmail);
        if (userPassword != null) userData.setPassword(userPassword);
        userClient.changeUserWithAuthorization(userData, token)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Изиенение данных пользователя без авторизацией")
    public void userWithoutAuthTest() {
        if (userName != null) userData.setName(userName);
        if (userEmail != null) userData.setEmail(userEmail);
        if (userPassword != null) userData.setPassword(userPassword);
        userClient.changeUserWithoutAuthorization(userData)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message",equalTo("You should be authorised"));
    }
}
