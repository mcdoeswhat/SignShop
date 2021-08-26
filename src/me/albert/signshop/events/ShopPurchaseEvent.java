package me.albert.signshop.events;

import me.albert.signshop.utils.PurChaseResult;
import me.albert.signshop.utils.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ShopPurchaseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private PurChaseResult purChaseResult;
    private Shop shop;
    private Player user;
    private int amount;
    private ItemStack item;

    public ShopPurchaseEvent(PurChaseResult purChaseResult, Shop shop, Player user, int amount, ItemStack item) {
        super();
        this.purChaseResult = purChaseResult;
        this.shop = shop;
        this.user = user;
        this.amount = amount;
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PurChaseResult getPurChaseResult() {
        return purChaseResult;
    }

    public Shop getShop() {
        return shop;
    }

    public Player getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getItem() {
        return item;
    }
}
