package me.kiiya.hotbarmanager;

import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.commands.MenuCommand;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.listeners.JoinLeaveListener;
import me.kiiya.hotbarmanager.support.bedwars1058.BedWars1058;
import me.kiiya.hotbarmanager.support.bedwars1058.BedWarsProxy;
import me.kiiya.hotbarmanager.support.bedwars2023.BedWars2023;
import me.kiiya.hotbarmanager.support.bedwars2023.BedWarsProxy2023;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class HotbarManager extends JavaPlugin {

    public static boolean compassAddon = false;

    public static me.kiiya.hotbarmanager.api.HotbarManager api;
    public static com.andrei1058.bedwars.api.BedWars bw1058Api;
    public static com.tomkeuper.bedwars.api.BedWars bw2023Api;
    public static MainConfig mainConfig;
    public static Support support;
    private Database db;

    @Override
    public void onEnable() {
        loadSupport();
        new Metrics(this, 20334);
        api = new API();

        Bukkit.getServicesManager().register(me.kiiya.hotbarmanager.api.HotbarManager.class, api, this, ServicePriority.Normal);
        getServer().getPluginCommand("hbm").setExecutor(new MenuCommand());
    }

    @Override
    public void onDisable() {

    }

    private void loadSupport() {
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") != null) {
            new BedWars1058();
        } else if (Bukkit.getPluginManager().getPlugin("BedWars2023") != null) {
            new BedWars2023();
        } else if (Bukkit.getPluginManager().getPlugin("BedWarsProxy") != null) {
            new BedWarsProxy();
        } else if (Bukkit.getPluginManager().getPlugin("BWProxy2023") != null) {
            new BedWarsProxy2023();
        }
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    public static HotbarManager getPlugins() {
        return HotbarManager.getPlugin(HotbarManager.class);
    }

    public static me.kiiya.hotbarmanager.api.HotbarManager getAPI() {
        return api;
    }

    public static Support getSupport() {
        return support;
    }

    public static com.tomkeuper.bedwars.api.BedWars getBW2023Api() {
        return bw2023Api;
    }

    public static com.andrei1058.bedwars.api.BedWars getBW1058Api() {
        return bw1058Api;
    }

    public static com.tomkeuper.bedwars.proxy.api.BedWars getBWProxy2023Api() {
        return com.tomkeuper.bedwars.proxy.BedWarsProxy.getAPI();
    }

    public static com.andrei1058.bedwars.proxy.api.BedWars getBWProxyApi() {
        return com.andrei1058.bedwars.proxy.BedWarsProxy.getAPI();
    }

    public void setDB(Database db) {
        this.db = db;
    }

    public Database getDB() {
        return db;
    }

    public static boolean isCompassAddon() {
        return compassAddon;
    }

}
