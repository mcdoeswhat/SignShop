package me.albert.signshop.gui;

import me.albert.signshop.utils.Shop;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopUseHolder implements InventoryHolder {
    private Shop shop;
    private List<ItemStack> container;

    public ShopUseHolder(Shop shop, List<ItemStack> container) {
        this.shop = shop;
        this.container = container;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public Shop getShop() {
        return shop;
    }

    public List<ItemStack> getContainer() {
        return container;
    }
}
