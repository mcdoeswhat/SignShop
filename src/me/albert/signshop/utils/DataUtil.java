package me.albert.signshop.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class DataUtil {
    public static String itemToString(ItemStack itemStack) {
        itemStack.setAmount(1);
        try {
            return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
        } catch (Exception ignored) {

        }
        return itemToStringOld(itemStack);
    }

    public static ItemStack stringToItem(String stringBlob) {
        try {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(stringBlob));
        } catch (Exception ignored) {
        }
        return stringToItemOld(stringBlob);
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
