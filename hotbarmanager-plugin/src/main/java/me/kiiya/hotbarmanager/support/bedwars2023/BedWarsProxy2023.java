package me.kiiya.hotbarmanager.support.bedwars2023;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.proxy2023.ProxyMessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWarsProxy2023 {
    public BedWarsProxy2023() {
        start();
    }

    public void start() {
        support = Support.BEDWARSPROXY2023;

        connectDatabase();
        loadConfig();
        loadMessages();
        loadCommands();
        loadListeners();
    }

    public void connectDatabase() {
        Utility.info("&eConnecting to database...");
        if (Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getBoolean("database.enable")) {
            db = new MySQL();
        } else {
            Utility.info("&cYou need MySQL to use this plugin with BedWarsProxy! Disabling...");
            Bukkit.getPluginManager().disablePlugin(HotbarManager.getPlugins());
        }
        Utility.info("&aDatabase connected!");
    }

    public void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getPlugins(), "config", Bukkit.getWorldContainer().getPath() + "/plugins/BWProxy2023/Addons/HotbarManager");
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
