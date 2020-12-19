package me.albert.signshop.taks;

import me.albert.signshop.SignShop;
import me.albert.signshop.gui.ShopUseHolder;
import me.albert.signshop.utils.ItemUtil;
import me.albert.signshop.utils.Shop;
import me.albert.signshop.utils.ShopType;
import me.albert.signshop.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopGUIRenderTask {
    public static void start() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SignShop.instance, () -> {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasMetadata("sign_shop_opening")) {
                        continue;
                    }
                    Inventory inventory = player.getOpenInventory().getTopInventory();
                    if (inventory == null) {
                        continue;
                    }
                    if (!(inventory.getHolder() instanceof ShopUseHolder)) {
                        continue;
                    }
                    ShopUseHolder holder = (ShopUseHolder) inventory.getHolder();
                    if (holder.getShop().getSign().getLocation().getWorld() != player.getWorld()
                            || holder.getShop().getSign().getLocation().distance(player.getLocation()) > 100) {
                        player.closeInventory();
                        player.sendMessage("§c检测到您距离商店过远,界面已自动关闭");
                        continue;
                    }
                    Shop shop = holder.getShop();
                    ItemStack ranOut = ItemUtil.make(Material.BARRIER, "§c§l已空", "§c此商店内的物品已被抽光");
                    if (holder.getShop().getShopType().equals(ShopType.CRATE)) {
                        List<ItemStack> items = new ArrayList<>();
                        ItemStack current = inventory.getItem(13);
                        boolean similar = false;
                        for (ItemStack stack : holder.getContainer()) {
                            if (stack != null && !stack.getType().equals(Material.AIR)) {
                                if (stack.isSimilar(current)) {
                                    similar = true;
                                    continue;
                                }
                                items.add(stack);
                            }
                        }
                        if (items.size() == 0 && !similar) {
                            inventory.setItem(13, ranOut);
                            continue;
                        }
                        if (items.size() == 0) {
                            continue;
                        }
                        ItemStack random = items.get(new Random().nextInt(items.size())).clone();
                        random.setAmount(1);
                        inventory.setItem(13, random);
                        ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§6§l抽奖商店",
                                "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                                , "§a出售商店内的随机物品", "§a商店库存: §e" + Utils.getItemAmount(holder.getContainer()), "§7点击购买");
                        inventory.setItem(22, info);
                    }
                    if (holder.getShop().getShopType().equals(ShopType.SELL)) {
                        int store = Utils.getItemAmount(holder.getContainer(), shop.getShopItem());
                        ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§b§l出售商店",
                                "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                                , "§a商店库存: §e" + store, "§a点击购买1个", "§7右键购买自定义数量");
                        inventory.setItem(22, info);
                    }
                    if (holder.getShop().getShopType().equals(ShopType.BUY)) {
                        ItemStack info = ItemUtil.make(Material.GOLD_NUGGET, "§2§l收购商店",
                                "§a单个物品价格: §e" + Utils.format(shop.getPrice()) + " " + shop.getPriceType().getName()
                                , "§a剩余空间: §e" + Utils.getEmptySlots(holder.getContainer()) + " §a格", "§a点击出售1个", "§7Shift+左键出售背包所有");
                        inventory.setItem(22, info);
                    }
                }
            } catch (Exception e) {
                if (SignShop.instance.getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                }

            }
            start();
        }, 10);
    }
}
