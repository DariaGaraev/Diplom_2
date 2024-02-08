package com.example.data;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public static UserData getRandomUser() {
        String name = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String email = RandomStringUtils.randomAlphabetic(10)+ "@google.com";

        return new UserData(name, password, email);
    }

}
