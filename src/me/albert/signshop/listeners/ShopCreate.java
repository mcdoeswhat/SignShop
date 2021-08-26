package me.albert.signshop.listeners;

import me.albert.signshop.SignShop;
import me.albert.signshop.gui.ShopCreateGUI;
import me.albert.signshop.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ShopCreate implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChange(SignChangeEvent event) {
        Block attache = Utils.getAttache(event.getBlock());
        if (attache == null || !(attache.getState() instanceof Container)) {
            return;
        }
        if (!SignShop.instance.getConfig().getStringList("blocks").contains(attache.getType().toString())) {
            return;
        }
        if (event.getLines().length > 0) {
            if (event.getLine(0).equalsIgnoreCase(SignShop.instance.getConfig().getString("create"))) {
                ShopCreateGUI.open(event.getPlayer(), event.getBlock().getLocation());
            }

        }


    }
}
