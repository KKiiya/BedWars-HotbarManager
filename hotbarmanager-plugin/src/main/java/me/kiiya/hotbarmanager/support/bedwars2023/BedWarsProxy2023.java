package me.kiiya.hotbarmanager.support.bedwars2023;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.proxy2023.ProxyMessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWarsProxy2023 {

    public BedWarsProxy2023() {
        start();
    }

    private void start() {
        support = Support.BEDWARSPROXY2023;

        connectDatabase();
        loadConfig();
        HotbarManager.manager = me.kiiya.hotbarmanager.player.HotbarManager.init();
        loadMessages();
        loadCommands();
        loadListeners();
    }

    private void connectDatabase() {
        Utility.info("&eConnecting to database...");
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig();
        if (config.getBoolean("database.enable")) HotbarManager.getInstance().setDB(new MySQL());
        else {
            Utility.info("&cYou need MySQL to use this plugin with BedWarsProxy! Disabling...");
            Bukkit.getPluginManager().disablePlugin(HotbarManager.getInstance());
        }
        Utility.info("&aDatabase connected!");
    }

    private void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getInstance(), "config", Bukkit.getWorldContainer().getPath() + "/plugins/BWProxy2023/Addons/HotbarManager");
        Utility.info("&aConfig loaded!");
    }

    private void loadMessages() {
        Utility.info("&eLoading messages...");
        new ProxyMessagesData();
        Utility.info("&aMessages loaded!");
    }

    private void loadCommands() {
        Utility.info("&eLoading commands...");
        Utility.info("&aCommands loaded!");
    }

    private void loadListeners() {
        Utility.info("&eLoading listeners...");
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getInstance());
        Utility.info("&aListeners loaded!");
    }
}
