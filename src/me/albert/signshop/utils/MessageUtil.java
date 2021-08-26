package me.albert.signshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class MessageUtil {
    public static String getPotionName(PotionType type) {
        switch (type) {
            case INSTANT_DAMAGE:
                return "harming";
            case INSTANT_HEAL:
                return "healing";
            case REGEN:
                return "regeneration";
            case JUMP:
                return "leaping";
            case SPEED:
                return "swiftness";
            case UNCRAFTABLE:
                return "empty";
        }
        return type.toString().toLowerCase();
    }

    public static String getItemNamePath(ItemStack item) {
        Material material = item.getType();
        String path = material.getKey().getNamespace() + '.' + material.getKey().getKey();
        if (material.isBlock()) {
            path = "block." + path;
        } else {
            path = "item." + path;
        }
        if (material.equals(Material.POTION) || material.equals(Material.SPLASH_POTION) || material.equals(Material.LINGERING_POTION)) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            String namespace = getPotionName(meta.getBasePotionData().getType());
            path = path + ".effect." + namespace;
        }
        return path;
    }

}
