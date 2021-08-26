package me.albert.signshop.gui;

import me.albert.signshop.utils.ShopStep;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopHolder implements InventoryHolder {
    private Sign sign;
    private ShopStep shopStep;

    public ShopHolder(Sign sign, ShopStep shopStep) {
        this.sign = sign;
        this.shopStep = shopStep;

    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public ShopStep getShopStep() {
        return shopStep;
    }
}
