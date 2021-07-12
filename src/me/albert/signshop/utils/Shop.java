package me.albert.signshop.utils;

import me.albert.signshop.SignShop;
import me.albert.signshop.events.ShopPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class Shop {

    private UUID playerUUID;
    private ShopType shopType;
    private double price;
    private PriceType priceType;
    private ItemStack shopItem;
    private Sign sign;

    public Shop(Sign sign, UUID playerUUID, ShopType shopType, double price, PriceType priceType, ItemStack shopItem) {
        if (shopItem != null) {
            shopItem.setAmount(1);
        }
        price = Double.parseDouble(Utils.format(price));
        this.playerUUID = playerUUID;
        this.shopType = shopType;
        this.price = price;
        this.priceType = priceType;
        this.shopItem = shopItem;
        this.sign = sign;
    }

    public ShopPurchaseEvent purchase(Player player, int amount) {
        if (amount <= 0) {
            player.sendMessage("§c物品数量不能为0!");
            return null;
        }
        amount = Math.abs(amount);
        if (amount > 3456) {
            player.sendMessage("§c物品交易最多一次3456个!");
            return null;
        }
        Shop shop = ShopUtil.getShop(sign.getBlock());
        if (shop == null || !shop.isSimilar(this)) {
            player.sendMessage("§c操作无效,商店已被摧毁?");
            return null;
        }
        if (player.getLocation().getWorld() != sign.getLocation().getWorld()
                || player.getLocation().distance(sign.getLocation()) > 100) {
            player.sendMessage("§c距离商店过远....");
            return null;
        }
        Block attache = Utils.getAttache(sign.getBlock());
        if (attache == null || !(attache.getState() instanceof Container)) {
            player.sendMessage("§c错误! 商店方块并非容器,无法使用商店");
            return null;
        }
        if (!SignShop.instance.getConfig().getStringList("blocks").contains(attache.getType().toString())) {
            player.sendMessage("§c错误! 此容器已被禁止作为商店使用");
            return null;
        }
        Container container = (Container) attache.getState();
        //出售
        if (shopType.equals(ShopType.SELL)) {
            if (shopItem == null) {
                player.sendMessage("§c商店主人还尚未设置此商店出售的物品.....");
                return null;
            }
            if (!container.getInventory().containsAtLeast(shopItem, amount)) {
                player.sendMessage("§c商店库存不足!");
                return null;
            }
            if (!Utils.hasSpace(shopItem, player.getInventory(), amount)) {
                player.sendMessage("§c您的背包空间不足!");
                return null;
            }
            return getShopPurchaseEvent(player, amount, container.getInventory(), player.getInventory());
        }
        //抽奖
        if (shopType.equals(ShopType.CRATE)) {
            if (container.getInventory().isEmpty()) {
                player.sendMessage("§c商店库存已空!");
                return null;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§c您的背包空间不足!");
                return null;
            }
            PurChaseResult result = buy(player, amount);
            if (result == null) {
                return null;
            }
            ItemStack random = Utils.getRandomStack(container.getInventory());
            player.getInventory().addItem(random);
            return new ShopPurchaseEvent(result, this, player, amount, random);
        }
        //收购
        if (shopType.equals(ShopType.BUY)) {
            if (shopItem == null) {
                player.sendMessage("§c商店主人还尚未设置此商店收购的物品.....");
                return null;
            }
            if (!player.getInventory().containsAtLeast(shopItem, amount)) {
                player.sendMessage("§c您的背包没有这么多对应的收购物品!");
                return null;
            }
            if (!Utils.hasSpace(shopItem, container.getInventory(), amount)) {
                player.sendMessage("§c商店剩余空间不足!");
                return null;
            }
            return getShopPurchaseEvent(player, amount, player.getInventory(), container.getInventory());
        }
        return null;
    }

    private ShopPurchaseEvent getShopPurchaseEvent(Player player, int amount, Inventory inventory, Inventory inventory2) {
        PurChaseResult result = buy(player, amount);
        if (result == null) {
            return null;
        }
        Utils.removeItems(inventory, shopItem, amount);
        Utils.addItems(inventory2, shopItem, amount);
        return new ShopPurchaseEvent(result, this, player, amount, shopItem);
    }

    private PurChaseResult buy(Player player, int amount) {
        double cost = price * amount;
        double balance = 0;
        OfflinePlayer owner = Bukkit.getOfflinePlayer(playerUUID);
        if (owner == null || owner.getName() == null) {
            SignShop.instance.getLogger().warning("未知离线用户: " + playerUUID);
            player.sendMessage("§c购买失败! 商店创建者不存在!");
            return null;
        }
        if (priceType == PriceType.MONEY) {
            balance = SignShop.getEconomy().getBalance(player);
            if (shopType == ShopType.BUY) {
                balance = SignShop.getEconomy().getBalance(owner);
            }
        }
        if (priceType == PriceType.POINTS) {
            balance = SignShop.getPp().look(player.getUniqueId());
            if (shopType == ShopType.BUY) {
                balance = SignShop.getPp().look(owner.getUniqueId());
            }
        }
        if (balance < cost) {
            if (shopType == ShopType.BUY) {
                player.sendMessage("§c此商店主人的" + priceType.getName() + "余额不足! 需要: " + cost + priceType.getName() + " 他拥有" + balance + priceType.getName());
                return null;
            }
            player.sendMessage("§c您的" + priceType.getName() + "余额不足! 需要: " + cost + priceType.getName() + " 您拥有" + balance + priceType.getName());
            return null;
        }
        double afterTax = Math.round((cost - (cost * SignShop.tax)) * 100.00) / 100.00;
        if (priceType == PriceType.MONEY) {
            if (shopType == ShopType.BUY) {
                SignShop.getEconomy().withdrawPlayer(owner, cost);
                SignShop.getEconomy().depositPlayer(player, afterTax);
                return new PurChaseResult(cost, afterTax);
            }
            SignShop.getEconomy().withdrawPlayer(player, cost);
            SignShop.getEconomy().depositPlayer(owner, afterTax);
            return new PurChaseResult(cost, afterTax);
        }
        if (priceType == PriceType.POINTS) {
            if (shopType == ShopType.BUY) {
                SignShop.getPp().take(playerUUID, (int) cost);
                afterTax = Math.round(((int) cost - ((int) cost * SignShop.tax)));
                SignShop.getPp().give(player.getUniqueId(), (int) afterTax);
                return new PurChaseResult((int) cost, (int) afterTax);
            }
            SignShop.getPp().take(player.getUniqueId(), (int) cost);
            afterTax = Math.round(((int) cost - ((int) cost * SignShop.tax)));
            SignShop.getPp().give(playerUUID, (int) afterTax);
            return new PurChaseResult((int) cost, (int) afterTax);
        }
        return null;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSimilar(Shop shop) {
        if (shop == null) {
            return false;
        }
        if (shop.getPrice() == price && playerUUID.equals(shop.getPlayerUUID())) {
            if (shop.getPriceType() == priceType) {
                if (shop.getShopType() == shopType) {
                    if (shopItem == null && shop.getShopItem() == null) {
                        return true;

                    } else return shopItem != null && shopItem.isSimilar(shop.getShopItem());

                }

            }
        }
        return false;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public ItemStack getShopItem() {
        return shopItem;
    }

    public void setShopItem(ItemStack shopItem) {
        this.shopItem = shopItem;
        sign.getPersistentDataContainer().set(Keys.shopItemKey, PersistentDataType.STRING, DataUtil.itemToString(shopItem));
        sign.update();
    }

    public Sign getSign() {
        return sign;
    }
}
