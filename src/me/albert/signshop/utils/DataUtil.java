package me.albert.signshop.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class DataUtil {
    public static String itemToString(ItemStack itemStack) {
        itemStack.setAmount(1);
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    public static ItemStack stringToItem(String stringBlob) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringBlob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config.getItemStack("item", null);
    }
}
