package me.kiiya.hotbarmanager.support.bedwars1058;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.config.ConfigManager;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.proxy.ProxyMessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.menu.helpers.CacheManager;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWarsProxy {

    public BedWarsProxy() {
        start();
    }

    private void start() {
        support = Support.BEDWARSPROXY;

        connectDatabase();
        loadConfig();
        HotbarManager.manager = me.kiiya.hotbarmanager.player.HotbarManager.init();
        if (manager.getSortType() == SortType.ITEM) {
            IShopCacheManager cacheManager = new CacheManager("default", HotbarManager.getVersionSupport());
            ConfigManager configManager = new ConfigManager(HotbarManager.getInstance(), "shop.yml", Bukkit.getWorldContainer().getPath() + "/plugins/BedWarsProxy/Addons/HotbarManager");
            cacheManager.loadFromConfig(configManager.getYml().getConfigurationSection(""));
        }
        loadMessages();
        loadCommands();
        loadListeners();
    }

    private void connectDatabase() {
        Utility.info("&eConnecting to database...");
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig();
        if (config.getBoolean("database.enable")) HotbarManager.getInstance().setDB(new MySQL());
        else {
            Utility.info("&cYou need MySQL to use this plugin with BedWarsProxy! Disabling...");
            Bukkit.getPluginManager().disablePlugin(HotbarManager.getInstance());
        }
        Utility.info("&aDatabase connected!");
    }

    private void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getInstance(), "config", Bukkit.getWorldContainer().getPath() + "/plugins/BedWarsProxy/Addons/HotbarManager");
        HotbarManager.debug = mainConfig.getBoolean("debug");
        HotbarUtils.initialize(mainConfig);
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
