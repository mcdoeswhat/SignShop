package me.albert.signshop.utils;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GUIUtil {
    public static InventoryHolder getHolder(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return null;
        }
        if (current.getType().equals(Material.AIR)) {
            return null;
        }
        if (event.getClickedInventory() == null) {
            return null;
        }
        return event.getInventory().getHolder();

    }
}
