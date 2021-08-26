package me.albert.signshop.utils;

public enum PriceType {
    POINTS("点券"), MONEY("游戏币");
    private String name;

    PriceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
