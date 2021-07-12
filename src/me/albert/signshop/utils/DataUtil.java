package me.albert.signshop.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class DataUtil {
    public static String itemToString(ItemStack itemStack) {
        itemStack.setAmount(1);
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    public static ItemStack stringToItem(String stringBlob) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(stringBlob));
    }

    public static String itemToStringOld(ItemStack itemStack) {
        itemStack.setAmount(1);
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    public static ItemStack stringToItemOld(String stringBlob) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringBlob);
        } catch (Exception e) {
            return null;
        }
        return config.getItemStack("item", null);
    }
}
