package me.kiiya.hotbarmanager.support.bedwars1058;

import com.andrei1058.bedwars.api.BedWars;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.bedwars1058.MessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.database.providers.SQLite;
import me.kiiya.hotbarmanager.listeners.bedwars1058.PlayerKill;
import me.kiiya.hotbarmanager.listeners.bedwars1058.RespawnListener;
import me.kiiya.hotbarmanager.listeners.bedwars1058.ShopBuy;
import me.kiiya.hotbarmanager.listeners.bedwars1058.ShopOpen;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;

import java.io.File;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWars1058 {
    public BedWars1058() {
        start();
    }

    public void start() {
        support = Support.BEDWARS1058;
        bw1058Api = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        if (Bukkit.getPluginManager().getPlugin("BedWars1058-Compass") != null) {
            compassAddon = true;
        }

        connectDatabase();
        loadConfig();
        loadMessages();
        loadCommands();
        loadListeners();
    }

    public void connectDatabase() {
        Utility.info("&eConnecting to database...");
        if (HotbarManager.getBW1058Api().getConfigs().getMainConfig().getBoolean("database.enable")) {
            HotbarManager.db = new MySQL();
        } else {
            HotbarManager.db = new SQLite();
        }
        Utility.info("&aDatabase connected!");
    }

    public void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getPlugins(), "config", bw1058Api.getAddonsPath().getPath() + File.separator + "HotbarManager");
        Utility.info("&aConfig loaded!");
    }

    public void loadMessages() {
        Utility.info("&eLoading messages...");
        new MessagesData();
        Utility.info("&aMessages loaded!");
    }

    public void loadCommands() {
        Utility.info("&eLoading commands...");
        Utility.info("&aCommands loaded!");
    }

    public void loadListeners() {
        Utility.info("&eLoading listeners...");
        Bukkit.getPluginManager().registerEvents(new ShopBuy(), getPlugins());
        Bukkit.getPluginManager().registerEvents(new ShopOpen(), getPlugins());
        Bukkit.getPluginManager().registerEvents(new PlayerKill(), getPlugins());
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), getPlugins());
        Utility.info("&aListeners loaded!");
    }
}
