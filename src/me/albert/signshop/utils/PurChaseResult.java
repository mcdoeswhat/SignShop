package me.albert.signshop.utils;

public class PurChaseResult {

    private double price;
    private double received;

    public PurChaseResult(double price, double received) {
        this.price = price;
        this.received = received;
    }

    public double getReceived() {
        return received;
    }

    public double getPrice() {
        return price;
    }


}
