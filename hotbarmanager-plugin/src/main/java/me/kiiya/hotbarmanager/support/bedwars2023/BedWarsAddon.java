package me.kiiya.hotbarmanager.support.bedwars2023;

import com.tomkeuper.bedwars.api.addon.Addon;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.config.ConfigManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.bedwars2023.MessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.database.providers.SQLite;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.listeners.CustomItemSecurity;
import me.kiiya.hotbarmanager.listeners.bedwars2023.*;
import me.kiiya.hotbarmanager.listeners.bedwars2023.category.RespawnListenerC;
import me.kiiya.hotbarmanager.listeners.bedwars2023.category.ShopBuyC;
import me.kiiya.hotbarmanager.listeners.bedwars2023.item.RespawnListenerI;
import me.kiiya.hotbarmanager.listeners.bedwars2023.item.ShopBuyI;
import me.kiiya.hotbarmanager.menu.helpers.CacheManager;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWarsAddon extends Addon {
    @Override
    public String getAuthor() {
        return "Kiiya";
    }

    @Override
    public Plugin getPlugin() {
        return HotbarManager.getInstance();
    }

    @Override
    public String getVersion() {
        return getPlugin().getDescription().getVersion();
    }

    @Override
    public String getName() {
        return "Hotbar Manager";
    }

    @Override
    public String getDescription() {
        return getPlugin().getDescription().getDescription();
    }

    @Override
    public void load() {
        Utility.info("LOADING BEDWARS2023 SUPPORT");
        compassAddon = Bukkit.getPluginManager().getPlugin("BedWars1058-Compass") != null;

        connectDatabase();
        loadConfig();
        HotbarManager.manager = me.kiiya.hotbarmanager.player.HotbarManager.init();
        if (manager.getSortType() == SortType.ITEM) {
            File shopsFolder = new File(HotbarManager.getInstance().getDataFolder().getParentFile(), "BedWars2023/Shops/");

            if (!shopsFolder.exists() || shopsFolder.listFiles() == null) {
                Utility.info("No shops found in /plugins/BedWars2023/Shops! Hotbar Manager requires shops to be sorted by item. Disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(HotbarManager.getInstance());
                return;
            }

            // Load default shop
            File defaultShopFile = new File(shopsFolder, "default-shop.yml");
            if (defaultShopFile.exists()) {
                ConfigManager defaultShop = new ConfigManager(HotbarManager.getInstance(), "default-shop", shopsFolder.getPath());
                CacheManager defaultCache = new CacheManager("default", HotbarManager.getVersionSupport());

                defaultCache.loadFromConfig(defaultShop.getYml());
            }

            // Load all other shops
            for (File shopFile : Objects.requireNonNull(shopsFolder.listFiles())) {
                String fileName = shopFile.getName().toLowerCase();
                if (!fileName.endsWith(".yml")) continue;
                if (!fileName.contains("-shop")) continue;
                if (fileName.equalsIgnoreCase("default-shop.yml")) continue;

                String shopName = fileName.replace("-shop.yml", "");
                ConfigManager shopConfig = new ConfigManager(HotbarManager.getInstance(), fileName.replace(".yml", ""), shopsFolder.getPath());
                CacheManager cache = new CacheManager(shopName, HotbarManager.getVersionSupport());
                cache.loadFromConfig(shopConfig.getYml());
            }
        }
        loadMessages();
        loadCommands();
        loadListeners();
    }

    @Override
    public void unload() {
        me.kiiya.hotbarmanager.player.HotbarManager.getInstance().saveHotbars(true, false);
        Bukkit.getPluginManager().disablePlugin(getPlugin());
    }

    private void connectDatabase() {
        Utility.info("&eConnecting to database...");
        Database db;
        YamlConfiguration config = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getYml();

        if (config.getString("database.type").equalsIgnoreCase("mysql")) db = new MySQL();
        else db = new SQLite();

        HotbarManager.getInstance().setDB(db);
        Utility.info("&aDatabase connected!");
    }

    private void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getInstance(), "config", bw2023Api.getAddonsPath().getPath() + File.separator + "HotbarManager");
        HotbarManager.debug = mainConfig.getBoolean("debug");
        HotbarUtils.initialize(mainConfig);
        Utility.info("&aConfig loaded!");
    }

    private void loadMessages() {
        Utility.info("&eLoading messages...");
        new MessagesData();
        Utility.info("&aMessages loaded!");
    }

    private void loadCommands() {
        Utility.info("&eLoading commands...");
        Utility.info("&aCommands loaded!");
    }

    private void loadListeners() {
        Utility.info("&eLoading listeners...");
        Bukkit.getPluginManager().registerEvents(new ShopOpen(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerKill(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getInstance());
        switch (manager.getSortType()) {
            case CATEGORY:
                Bukkit.getPluginManager().registerEvents(new ShopBuyC(), getPlugin());
                Bukkit.getPluginManager().registerEvents(new RespawnListenerC(), getPlugin());
                break;
            case ITEM:
                Bukkit.getPluginManager().registerEvents(new ShopBuyI(), getPlugin());
                Bukkit.getPluginManager().registerEvents(new RespawnListenerI(), getPlugin());
                break;
        }

        if (bw2023Api.getVersionSupport().getVersion() == 0) Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.Legacy(), getInstance());
        else Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.New(), getInstance());

        Utility.info("&aListeners loaded!");
    }
}
