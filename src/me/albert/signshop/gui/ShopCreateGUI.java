package me.albert.signshop.gui;

import com.google.common.util.concurrent.AtomicDouble;
import me.albert.signshop.SignShop;
import me.albert.signshop.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopCreateGUI implements Listener {

    public static ConcurrentHashMap<UUID, Inventory> setPrices = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        System.out.println(Math.random());

    }

    public static void open(Player player, Location location) {
        if (!isSign(location)) {
            return;
        }
        Block block = location.getBlock();
        Sign sign = (Sign) block.getState();
        Inventory inventory = Bukkit.createInventory(new ShopHolder(sign, ShopStep.CREATE), 27, "§c创建商店-选择类型");
        ItemStack sell = ItemUtil.make(Material.DIAMOND, "§b§l出售", "§3出售特定物品");
        ItemStack buy = ItemUtil.make(Material.EMERALD, "§c§l收购", "§3收购特定物品");
        ItemStack crate = ItemUtil.make(Material.LAPIS_LAZULI, "§6§l抽奖", "§3随机出售箱子内物品");
        inventory.setItem(11, sell);
        inventory.setItem(13, buy);
        inventory.setItem(15, crate);
        player.openInventory(inventory);
    }

    public static boolean isSign(Location location) {
        Block block = location.getBlock();
        return block != null && block.getState() instanceof Sign;
    }

    public static void open(Player player, ShopType shopType, Sign sign) {
        if (!isSign(sign.getLocation())) {
            player.closeInventory();
            return;
        }
        Inventory inventory = Bukkit.createInventory(new ShopPriceHolder(sign, ShopStep.SET_PRICE, 0, PriceType.MONEY, shopType), 27, "§c创建商店-选择价格");
        renderPriceInv(inventory);
        player.openInventory(inventory);
    }

    public static void renderPriceInv(Inventory inventory) {
        ShopPriceHolder priceHolder = (ShopPriceHolder) inventory.getHolder();
        ItemStack money = ItemUtil.make(Material.DIAMOND, "§b§l游戏币", "§3§l设置价格类型为游戏币");
        ItemStack points = ItemUtil.make(Material.EMERALD, "§a§l点券", "§3§l设置价格类型为点券");
        money.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        points.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (priceHolder.getPriceType().equals(PriceType.MONEY)) {
            money.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            money = ItemUtil.make(money, "§b§l游戏币", "§3§l设置价格类型为游戏币", "§c当前");
        } else {
            points.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            points = ItemUtil.make(points, "§a§l点券", "§3§l设置价格类型为点券", "§c当前");
        }
        ItemStack value = ItemUtil.make(Material.SUNFLOWER, "§6§l设置价格", "§3设置您要出售/收购物品的价格", "§3点击设置");
        inventory.setItem(12, money);
        inventory.setItem(14, points);
        inventory.setItem(22, value);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        setPrices.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (setPrices.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Inventory inventory = setPrices.get(player.getUniqueId());
            setPrices.remove(player.getUniqueId());
            ShopPriceHolder priceHolder = (ShopPriceHolder) inventory.getHolder();
            double price;
            try {
                price = Double.parseDouble(event.getMessage());
            } catch (Exception ignored) {
                player.sendMessage("§c请输入正确的数字");
                return;
            }
            price = Math.abs(price);
            if (price > 1000000) {
                player.sendMessage("§c最大价格限制1000000!");
                return;
            }
            priceHolder.setPrice(price);
            player.sendMessage("§a设置价格成功!");
            renderPriceInv(inventory);
            AtomicDouble finalPrice = new AtomicDouble(price);
            Bukkit.getScheduler().runTask(SignShop.instance, () -> {
                if (!isSign(priceHolder.getSign().getLocation())) {
                    player.sendMessage("§c创建商店失败...牌子被摧毁?");
                    return;
                }
                String shopName = "§e" + player.getName();
                String line1 = "§b出售商店";
                String line2;
                String line3 = "§c右键查看/购买";
                if (priceHolder.getShopType().equals(ShopType.BUY)) {
                    line1 = "§a收购商店";
                    line3 = "§c右键查看/出售";
                }
                if (priceHolder.getShopType().equals(ShopType.CRATE)) {
                    line1 = "§5抽奖商店";
                    line3 = "§c右键查看/抽奖";
                }
                if (priceHolder.getPriceType().equals(PriceType.MONEY)) {
                    line2 = "§6" + Utils.format(priceHolder.getPrice()) + "游戏币";
                } else {
                    line2 = "§2" + (int) priceHolder.getPrice() + "点券";
                }
                if (priceHolder.getPriceType().equals(PriceType.POINTS)) {
                    if ((int) finalPrice.get() != finalPrice.get()) {
                        finalPrice.set((int) finalPrice.get());
                    }
                }
                Sign sign = (Sign) priceHolder.getSign().getBlock().getState();
                sign.setLine(0, shopName);
                sign.setLine(1, line1);
                sign.setLine(2, line2);
                sign.setLine(3, line3);
                sign.getPersistentDataContainer().set(Keys.uuidKey, PersistentDataType.STRING, player.getUniqueId().toString());
                sign.getPersistentDataContainer().set(Keys.priceKey, PersistentDataType.DOUBLE, finalPrice.get());
                sign.getPersistentDataContainer().set(Keys.priceTypeKey, PersistentDataType.STRING, priceHolder.getPriceType().toString());
                sign.getPersistentDataContainer().set(Keys.shopTypeKey, PersistentDataType.STRING, priceHolder.getShopType().toString());
                sign.update();
                player.sendMessage("§a§l商店创建成功!");
                if (priceHolder.getShopType() != ShopType.CRATE) {
                    player.sendMessage("§c请使用要收购/出售的物品左键牌子来设置要收购/出售的物品");
                }
            });

        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = GUIUtil.getHolder(event);
        if (!(holder instanceof ShopHolder)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory().getHolder() instanceof ShopHolder) {
            ShopHolder shopHolder = (ShopHolder) holder;
            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();
            if (shopHolder.getShopStep().equals(ShopStep.CREATE)) {
                if (slot == 11) {
                    open(player, ShopType.SELL, shopHolder.getSign());
                }
                if (slot == 13) {
                    open(player, ShopType.BUY, shopHolder.getSign());
                }
                if (slot == 15) {
                    open(player, ShopType.CRATE, shopHolder.getSign());
                }
            }
            if (shopHolder instanceof ShopPriceHolder) {
                ShopPriceHolder priceHolder = (ShopPriceHolder) shopHolder;
                if (slot == 12) {
                    priceHolder.setPriceType(PriceType.MONEY);
                    renderPriceInv(event.getInventory());
                    return;
                }
                if (slot == 14) {
                    priceHolder.setPriceType(PriceType.POINTS);
                    renderPriceInv(event.getInventory());
                    return;
                }
                if (slot == 22) {
                    player.closeInventory();
                    setPrices.put(player.getUniqueId(), event.getInventory());
                    player.sendMessage("§a请在聊天栏输入您想要设置的价格(单个物品)");
                }

            }


        }
    }
}
