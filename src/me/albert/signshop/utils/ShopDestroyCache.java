package me.albert.signshop.utils;

import me.albert.signshop.SignShop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopDestroyCache {
    public static ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<>();

    public static String putShop(Shop shop) {
        UUID uuid = UUID.nameUUIDFromBytes(shop.getSign().getLocation().toString().getBytes());
        Bukkit.getScheduler().runTaskAsynchronously(SignShop.instance, () -> shops.put(uuid.toString(), shop));
        return uuid.toString();
    }

    public static void destroy(Player player, String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(SignShop.instance, () -> {
            if (shops.containsKey(uuid)) {
                Shop shop = shops.get(uuid);
                if (!shop.getPlayerUUID().equals(player.getUniqueId())) {
                    player.sendMessage("§c商店的主人不是你!");
                    return;
                }
                Bukkit.getScheduler().runTask(SignShop.instance, () -> {
                    Block signBlock = shop.getSign().getBlock();
                    if (!shop.isSimilar(ShopUtil.getShop(signBlock))) {
                        player.sendMessage("§c商店摧毁失败,商店无效或已被摧毁");
                        return;
                    }
                    signBlock.breakNaturally();
                    player.sendMessage("§a商店已被成功摧毁!");
                    shops.remove(uuid);
                });
                return;
            }
            player.sendMessage("§c商店摧毁失败,商店无效或已被摧毁");
        });

    }
}
