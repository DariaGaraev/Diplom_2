package com.example.data;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public static UserData getRandomUser() {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);

        return new UserData(login, password, firstName);
    }
}
