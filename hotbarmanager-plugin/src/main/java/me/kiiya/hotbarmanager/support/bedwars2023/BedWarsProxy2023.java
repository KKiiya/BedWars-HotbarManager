package me.kiiya.hotbarmanager.support.bedwars2023;

import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.proxy2023.ProxyMessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.menu.helpers.CacheManager;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Objects;

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
        if (manager.getSortType() == SortType.ITEM) {
            File shopsFolder = new File("/plugins/BedWarsProxy/Shops");
            File defaultShopFile = new File(shopsFolder, "default-shop.yml");
            ConfigManager defaultShop = new ConfigManager(HotbarManager.getInstance(), "default-shop.yml", shopsFolder.getPath());
            IShopCacheManager cs = new CacheManager("default", HotbarManager.getVersionSupport());
            cs.loadFromConfig(defaultShop.getYml().getConfigurationSection(""));

            if (!shopsFolder.exists() || shopsFolder.listFiles() == null) {
                Utility.info("No shops found in /plugins/BedWars2023/Shops! Hotbar Manager requires shops to be sorted by item. Disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(HotbarManager.getInstance());
                return;
            }
            for (File shopFile : Objects.requireNonNull(shopsFolder.listFiles())) {
                String fileName = shopFile.getName().toLowerCase();
                if (!fileName.endsWith(".yml")) continue;
                if (!fileName.contains("-shop")) continue;
                if (shopFile.equals(defaultShopFile)) continue;
                ConfigManager shopConfig = new ConfigManager(HotbarManager.getInstance(), fileName, shopsFolder.getPath());
                IShopCacheManager shopCache = new CacheManager(fileName.replace("-shop.yml", ""), HotbarManager.getVersionSupport());
                shopCache.loadFromConfig(shopConfig.getYml().getConfigurationSection(""));
            }
        }
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
