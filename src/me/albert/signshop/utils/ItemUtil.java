package me.albert.signshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

//author Albert
public class ItemUtil {
    public static ItemStack make(ItemStack is, String name, String... lore) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack make(Material material, String name, String... lore) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack make(Material material, String name, List<String> lore) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack make(ItemStack is, String name, List<String> lore) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
