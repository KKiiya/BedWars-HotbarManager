package me.kiiya.hotbarmanager.support.bedwars1058;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.proxy.ProxyMessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWarsProxy {
    public BedWarsProxy() {
        start();
    }

    public void start() {
        support = Support.BEDWARSPROXY;

        connectDatabase();
        loadConfig();
        loadMessages();
        loadCommands();
        loadListeners();
    }

    public void connectDatabase() {
        Utility.info("&eConnecting to database...");
        if (Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getBoolean("database.enable")) {
            db = new MySQL();
        } else {
            Utility.info("&cYou need MySQL to use this plugin with BedWarsProxy! Disabling...");
            Bukkit.getPluginManager().disablePlugin(HotbarManager.getPlugins());
        }
        Utility.info("&aDatabase connected!");
    }

    public void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getPlugins(), "config", Bukkit.getWorldContainer().getPath() + "/plugins/BedWarsProxy/Addons/HotbarManager");
        Utility.info("&aConfig loaded!");
    }

    public void loadMessages() {
        Utility.info("&eLoading messages...");
        new ProxyMessagesData();
        Utility.info("&aMessages loaded!");
    }

    public void loadCommands() {
        Utility.info("&eLoading commands...");
        Utility.info("&aCommands loaded!");
    }

    public void loadListeners() {
        Utility.info("&eLoading listeners...");
        Utility.info("&aListeners loaded!");
    }
}
