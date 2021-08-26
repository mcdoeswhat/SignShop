package me.albert.signshop.gui;

import me.albert.signshop.utils.PriceType;
import me.albert.signshop.utils.ShopStep;
import me.albert.signshop.utils.ShopType;
import org.bukkit.block.Sign;

public class ShopPriceHolder extends ShopHolder {
    private double price;
    private PriceType priceType;
    private ShopType shopType;

    public ShopPriceHolder(Sign sign, ShopStep shopStep, double price, PriceType priceType, ShopType shopType) {
        super(sign, shopStep);
        this.price = price;
        this.priceType = priceType;
        this.shopType = shopType;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ShopType getShopType() {
        return shopType;
    }
}
