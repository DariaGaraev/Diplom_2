package com.example.data;

import java.util.ArrayList;
import java.util.List;

public class OrderGenerator {

    public static OrderData getRandomOrder() {
        OrderData order = new OrderData();
        List<String> ingridients = new ArrayList<>();
        ingridients.add("61c0c5a71d1f82001bdaaa6f");
        order.setIngredients(ingridients);
        return order;
    }

}