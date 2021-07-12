package me.albert.signshop.listeners;

import me.albert.signshop.SignShop;
import me.albert.signshop.events.ShopPurchaseEvent;
import me.albert.signshop.utils.MessageUtil;
import me.albert.signshop.utils.Shop;
import me.albert.signshop.utils.ShopDestroyCache;
import me.albert.signshop.utils.ShopType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ShopPurchase implements Listener {

    public static TextComponent getShopInfo(Shop shop, String uuid) {
        TextComponent textComponent = new TextComponent("§f§n商店");
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("§3§l商店信息"),
                new Text("\n§7世界: " + shop.getSign().getLocation().getWorld().getName()),
                new Text("\n§7X: " + shop.getSign().getLocation().getBlockX()),
                new Text("\n§7Y: " + shop.getSign().getLocation().getBlockY()),
                new Text("\n§7Z: " + shop.getSign().getLocation().getBlockZ()),
                new Text("\n§c[点击摧毁此商店] ")
        ));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/signshop destroyshop " + uuid));
        return textComponent;
    }

    @EventHandler
    public void onPurchase(ShopPurchaseEvent event) {
        Player user = event.getUser();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(event.getShop().getPlayerUUID());
        Shop shop = event.getShop();
        double cost = event.getPurChaseResult().getPrice();
        double afterTax = event.getPurChaseResult().getReceived();
        String shopUUID = ShopDestroyCache.putShop(shop);
        if (shop.getShopType().equals(ShopType.SELL)) {
            TextComponent component = new TextComponent("§a你从" + owner.getName() + "的商店里买走了: §e" + event.getAmount() + " X ");
            addItem(component, event.getItem());
            component.addExtra("§a 花费: §e" + cost + shop.getPriceType().getName());
            user.sendMessage(component);
            if (owner.isOnline()) {
                component = new TextComponent("§a" + user.getName() + "从你的");
                component.addExtra(getShopInfo(shop, shopUUID));
                component.addExtra("§a里买走了: §e" + event.getAmount() + " X ");
                addItem(component, event.getItem());
                component.addExtra("§a 你赚取了: §e" + afterTax + shop.getPriceType().getName() + "§7(税后)");
                owner.getPlayer().sendMessage(component);
            }
        }
        if (shop.getShopType().equals(ShopType.BUY)) {
            TextComponent component = new TextComponent("§a你向" + owner.getName() + "的商店里出售了: §e" + event.getAmount() + " X ");
            addItem(component, event.getItem());
            component.addExtra("§a 赚取了: §e" + afterTax + shop.getPriceType().getName() + "§7(税后)");
            user.sendMessage(component);
            if (owner.isOnline()) {
                component = new TextComponent("§a" + user.getName() + "向你的");
                component.addExtra(getShopInfo(shop, shopUUID));
                component.addExtra("§a里出售了: §e" + event.getAmount() + " X ");
                addItem(component, event.getItem());
                component.addExtra("§a 从你的账户里扣除了: §e" + cost + shop.getPriceType().getName());
                owner.getPlayer().sendMessage(component);
            }
        }
        if (shop.getShopType().equals(ShopType.CRATE)) {
            TextComponent component = new TextComponent("§a你在" + owner.getName() + "的商店里抽中了: §e");
            addItem(component, event.getItem());
            component.addExtra("§a 花费: §e" + cost + shop.getPriceType().getName());
            user.sendMessage(component);
            if (owner.isOnline()) {
                component = new TextComponent("§a" + user.getName() + "在你的");
                component.addExtra(getShopInfo(shop, shopUUID));
                component.addExtra("§a里抽中了: §e" + event.getAmount() + " X ");
                addItem(component, event.getItem());
                component.addExtra("§a 你赚取了: §e" + afterTax + shop.getPriceType().getName() + "§7(税后)");
                owner.getPlayer().sendMessage(component);
            }

        }
        try {
            if (!SignShop.instance.getConfig().getBoolean("enable_sounds")) {
                return;
            }
            user.playSound(user.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 0.1f);
            owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 0.1f);
        } catch (Exception e) {
            if (SignShop.instance.getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
        }


    }

    public static void addItem(TextComponent component, ItemStack item) {
        if (item.getItemMeta().hasDisplayName()) {
            component.addExtra(item.getDisplayName());
        } else {
            component.addExtra(new TranslatableComponent(MessageUtil.getItemNamePath(item)));
        }

    }
}
