package me.albert.signshop.utils;

import me.albert.signshop.SignShop;
import org.bukkit.NamespacedKey;

public class Keys {
    public static NamespacedKey uuidKey = new NamespacedKey(SignShop.instance, "sign_shop_uuid");
    public static NamespacedKey priceKey = new NamespacedKey(SignShop.instance, "sign_shop_price");
    public static NamespacedKey priceTypeKey = new NamespacedKey(SignShop.instance, "sign_shop_price_type");
    public static NamespacedKey shopTypeKey = new NamespacedKey(SignShop.instance, "sign_shop_type");
    public static NamespacedKey shopItemKey = new NamespacedKey(SignShop.instance, "sign_shop_item");

}
