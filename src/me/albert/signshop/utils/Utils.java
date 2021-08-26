package me.albert.signshop.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static Block getAttache(Block b) {
        if (b == null) {
            return null;
        }
        if (!(b.getState().getBlockData() instanceof WallSign)) {
            return null;
        }
        WallSign wallSign = (WallSign) b.getState().getBlockData();
        return b.getRelative(wallSign.getFacing().getOppositeFace());
    }

    public static void removeItems(Inventory inventory, ItemStack itemStack, int amount) {
        itemStack.setAmount(1);
        for (int i = 0; i < amount; i++) {
            for (ItemStack invItem : inventory.getStorageContents()) {
                if (invItem != null && invItem.isSimilar(itemStack)) {
                    invItem.setAmount(invItem.getAmount() - 1);
                    break;
                }
            }
        }
    }

    public static int getItemAmount(Inventory inventory, ItemStack itemStack) {
        int amount = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item != null && item.isSimilar(itemStack)) {
                amount += item.getAmount();
            }

        }
        return amount;
    }

    public static int getItemAmount(Inventory inventory) {
        int amount = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item != null && item.getType() != Material.AIR) {
                amount += item.getAmount();
            }

        }
        return amount;
    }

    public static int getItemAmount(List<ItemStack> items, ItemStack itemStack) {
        int amount = 0;
        for (ItemStack item : items) {
            if (item != null && item.isSimilar(itemStack)) {
                amount += item.getAmount();
            }

        }
        return amount;
    }

    public static void addItems(Inventory inventory, ItemStack itemStack, int amount) {
        itemStack.setAmount(1);
        for (int i = 0; i < amount; i++) {
            inventory.addItem(itemStack);
        }
    }

    public static ItemStack getRandomStack(Inventory inventory) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack != null && !stack.getType().equals(Material.AIR)) {
                items.add(stack);
            }
        }
        ItemStack invStack = items.get(new Random().nextInt(items.size()));
        ItemStack result = invStack.clone();
        result.setAmount(1);
        invStack.setAmount(invStack.getAmount() - 1);
        return result;
    }

    public static String format(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("###################.##");
        return decimalFormat.format(d);
    }

    public static boolean hasSpace(ItemStack is, Inventory inventory, int amount) {
        int slots = (int) Math.ceil(amount / is.getMaxStackSize());
        if (amount % is.getMaxStackSize() != 0) {
            slots++;
        }
        if (slots == 0) {
            slots = 1;
        }
        return getEmptySlots(inventory) >= slots;
    }

    public static int getEmptySlots(Inventory inventory) {
        int amount = 0;
        for (ItemStack is : inventory.getStorageContents()) {
            if (is == null || is.getType() == Material.AIR) {
                amount++;
            }
        }
        return amount;
    }
}
