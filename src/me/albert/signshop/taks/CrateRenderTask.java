package me.albert.signshop.taks;

import me.albert.signshop.SignShop;
import me.albert.signshop.gui.ShopUseHolder;
import me.albert.signshop.utils.ItemUtil;
import me.albert.signshop.utils.ShopType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateRenderTask {
    public static void start() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SignShop.instance, () -> {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasMetadata("sign_shop_crate")) {
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
                    if (!holder.getShop().getShopType().equals(ShopType.CRATE)) {
                        continue;
                    }
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
                        ItemStack ranOut = ItemUtil.make(Material.BARRIER, "§c§l已空", "§c此商店内的物品已被抽光");
                        inventory.setItem(13, ranOut);
                        continue;
                    }
                    ItemStack random = items.get(new Random().nextInt(items.size())).clone();
                    random.setAmount(1);
                    inventory.setItem(13, random);
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
