package me.albert.signshop.listeners;

import me.albert.signshop.SignShop;
import me.albert.signshop.events.ShopPurchaseEvent;
import me.albert.signshop.gui.ShopUseHolder;
import me.albert.signshop.utils.GUIUtil;
import me.albert.signshop.utils.Shop;
import me.albert.signshop.utils.ShopType;
import me.albert.signshop.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopGUIClick implements Listener {
    private static ConcurrentHashMap<UUID, Shop> buys = new ConcurrentHashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        buys.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (buys.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            int amount;
            try {
                amount = Integer.parseInt(event.getMessage());
            } catch (Exception e) {
                player.sendMessage("§c请输入正整数!");
                buys.remove(player.getUniqueId());
                return;
            }
            Bukkit.getScheduler().runTask(SignShop.instance, () -> {
                Shop shop = buys.get(player.getUniqueId());
                buys.remove(player.getUniqueId());
                ShopPurchaseEvent purchaseEvent = shop.purchase(player, amount);
                if (purchaseEvent != null) {
                    Bukkit.getServer().getPluginManager().callEvent(purchaseEvent);

                }
            });

        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = GUIUtil.getHolder(event);
        if (!(holder instanceof ShopUseHolder)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory().getHolder() instanceof ShopUseHolder) {
            ShopUseHolder shopUseHolder = (ShopUseHolder) holder;
            Player player = (Player) event.getWhoClicked();
            Shop shop = shopUseHolder.getShop();
            int slot = event.getSlot();
            if (slot == event.getClickedInventory().getSize() - 5) {
                ShopPurchaseEvent purchaseEvent = null;
                boolean clicked = false;
                if (event.getClick().equals(ClickType.LEFT)) {
                    purchaseEvent = shop.purchase(player, 1);
                    clicked = true;
                }
                ItemStack shopItem = shop.getShopItem();
                if (shopItem != null) {
                    if (shop.getShopType() == ShopType.BUY) {
                        if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                            int itemAmount = Utils.getItemAmount(player.getInventory(), shopItem);
                            if (itemAmount == 0) {
                                player.sendMessage("§c你的背包一个没有发现任何对应的收购物品!");
                                player.closeInventory();
                                return;
                            }
                            purchaseEvent = shop.purchase(player, itemAmount);
                            clicked = true;
                        }
                    }
                    if (shop.getShopType() == ShopType.SELL) {
                        if (event.getClick().equals(ClickType.RIGHT)) {
                            buys.put(player.getUniqueId(), shop);
                            player.sendMessage("§a请在聊天栏输入您想要购买的物品数量");
                            player.closeInventory();
                            return;
                        }
                    }

                }

                if (!clicked) {
                    return;
                }
                if (purchaseEvent == null) {
                    player.closeInventory();
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(purchaseEvent);
            }
        }
    }
}
