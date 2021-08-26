package me.albert.signshop.listeners;


import me.albert.signshop.SignShop;
import me.albert.signshop.gui.ShopUseHolder;
import me.albert.signshop.utils.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class ShopInteract implements Listener {
    private static HashMap<UUID, Long> cds = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (ShopUtil.getShop(event.getBlock()) != null) {
            if (!event.getPlayer().isSneaking()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§c请按住Shift摧毁商店");
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        event.getPlayer().removeMetadata("sign_shop_opening", SignShop.instance);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Shop shop = ShopUtil.getShop(event.getClickedBlock());
        if (shop == null) return;
        Block attache = Utils.getAttache(shop.getSign().getBlock());
        Player player = event.getPlayer();
        if (cds.containsKey(player.getUniqueId())) {
            if (System.currentTimeMillis() - cds.get(player.getUniqueId()) < 400) {
                return;
            }
        }
        cds.put(player.getUniqueId(), System.currentTimeMillis());
        if (attache == null || !(attache.getState() instanceof Container)) {
            player.sendMessage("§c错误! 商店方块并非容器,无法使用商店");
            return;
        }
        Container container = (Container) attache.getState();
        //设置商店物品
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (!shop.getShopType().equals(ShopType.CRATE) && shop.getShopItem() == null && shop.getPlayerUUID().equals(player.getUniqueId())) {
                if (event.getItem() == null) {
                    return;
                }
                event.setCancelled(true);
                ItemStack item = event.getItem().clone();
                item.setAmount(1);
                shop.setShopItem(item);
                TextComponent message = new TextComponent("§a设置成功! 商店收购/出售的物品目前为: ");
                if (item.getItemMeta().hasDisplayName()) {
                    message.addExtra(item.getDisplayName());
                } else {
                    message.addExtra(new TranslatableComponent(MessageUtil.getItemNamePath(item)));
                }
                player.sendMessage(message);
            }
        }
        //使用商店
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //出售商店
            Inventory inventory = Bukkit.createInventory(new ShopUseHolder(shop, container.getInventory()), 27, "§3§l商店预览");
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            }
            if (shop.getShopType().equals(ShopType.SELL)) {
                ItemStack sellItem = shop.getShopItem();
                if (sellItem == null) {
                    player.sendMessage("§c商店主人还尚未设置此商店出售的物品.....");
                    return;
                }
                inventory.setItem(13, sellItem);
                ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§b§l出售商店",
                        "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                        , "§a商店库存: §e" + Utils.getItemAmount(container.getInventory(), shop.getShopItem()), "§a点击购买1个", "§7右键购买自定义数量");
                inventory.setItem(22, info);
                player.openInventory(inventory);
                player.setMetadata("sign_shop_opening", new FixedMetadataValue(SignShop.instance, true));
                return;
            }
            if (shop.getShopType().equals(ShopType.BUY)) {
                ItemStack sellItem = shop.getShopItem();
                if (sellItem == null) {
                    player.sendMessage("§c商店主人还尚未设置此商店收购的物品.....");
                    return;
                }
                inventory.setItem(13, sellItem);
                ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§2§l收购商店",
                        "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                        ,"§a剩余空间: §e"+Utils.getEmptySlots(container.getInventory())+" §a格", "§a点击出售1个", "§7Shift+左键出售背包所有");
                inventory.setItem(22, info);
                player.openInventory(inventory);
                player.setMetadata("sign_shop_opening", new FixedMetadataValue(SignShop.instance, true));
                return;
            }
            if (shop.getShopType().equals(ShopType.CRATE)) {
                ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§6§l抽奖商店",
                        "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                        , "§a出售商店内的随机物品", "§a商店库存: §e" + Utils.getItemAmount(container.getInventory()), "§7点击购买");
                inventory.setItem(22, info);
                player.openInventory(inventory);
                player.setMetadata("sign_shop_opening", new FixedMetadataValue(SignShop.instance, true));
            }

        }


    }
}
