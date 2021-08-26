package me.albert.signshop.gui;

import me.albert.signshop.utils.Shop;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopUseHolder implements InventoryHolder {
    private Shop shop;
    private Inventory container;

    public ShopUseHolder(Shop shop, Inventory container) {
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

    public Inventory getContainer() {
        return container;
    }
}
