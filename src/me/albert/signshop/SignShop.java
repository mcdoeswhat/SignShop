package me.albert.signshop;

import me.albert.signshop.gui.ShopCreateGUI;
import me.albert.signshop.listeners.ShopCreate;
import me.albert.signshop.listeners.ShopGUIClick;
import me.albert.signshop.listeners.ShopInteract;
import me.albert.signshop.listeners.ShopPurchase;
import me.albert.signshop.taks.ShopGUIRenderTask;
import me.albert.signshop.utils.ShopDestroyCache;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SignShop extends JavaPlugin implements Listener {
    public static SignShop instance;
    public static double tax;
    private static Economy econ;
    private static PlayerPointsAPI pp;

    public static PlayerPointsAPI getPp() {
        return pp;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (!setupEconomy()) {
            this.getLogger().severe("Vault not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        pp = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
        tax = getConfig().getDouble("tax");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ShopCreate(), this);
        getServer().getPluginManager().registerEvents(new ShopCreateGUI(), this);
        getServer().getPluginManager().registerEvents(new ShopInteract(), this);
        getServer().getPluginManager().registerEvents(new ShopGUIClick(), this);
        getServer().getPluginManager().registerEvents(new ShopPurchase(), this);
        getLogger().info("Loaded");
        ShopGUIRenderTask.start();
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("signshop.reload")) {
            reloadConfig();
            tax = getConfig().getDouble("tax");
            sender.sendMessage("§a配置文件已经重新载入");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("destroyshop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c玩家才能使用此命令");
                return true;
            }
            Player player = (Player) sender;
            ShopDestroyCache.destroy(player, args[1]);
            return true;
        }
        sender.sendMessage("§a/signshop reload");
        return true;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }


}
