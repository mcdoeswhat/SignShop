package me.albert.signshop.utils;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ShopUtil {
    public static Shop getShop(Block block) {
        if (Utils.getAttache(block) == null) {
            return null;
        }
        Sign sign = (Sign) block.getState();
        PersistentDataContainer container = sign.getPersistentDataContainer();
        if (!container.has(Keys.uuidKey, PersistentDataType.STRING)) {
            return null;
        }
        UUID playerID = UUID.fromString(container.get(Keys.uuidKey, PersistentDataType.STRING));
        double price = container.get(Keys.priceKey, PersistentDataType.DOUBLE);
        PriceType priceType = PriceType.valueOf(container.get(Keys.priceTypeKey, PersistentDataType.STRING));
        ShopType shopType = ShopType.valueOf(container.get(Keys.shopTypeKey, PersistentDataType.STRING));
        ItemStack shopItem = null;
        if (container.has(Keys.shopItemKey, PersistentDataType.STRING)) {
            shopItem = DataUtil.stringToItem(container.get(Keys.shopItemKey, PersistentDataType.STRING));
        }
        return new Shop(sign, playerID, shopType, price, priceType, shopItem);
    }
}
