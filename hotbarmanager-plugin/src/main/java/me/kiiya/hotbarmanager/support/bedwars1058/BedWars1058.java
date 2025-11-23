package me.kiiya.hotbarmanager.support.bedwars1058;

import com.andrei1058.bedwars.api.BedWars;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.bedwars1058.MessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.database.providers.SQLite;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.listeners.CustomItemSecurity;
import me.kiiya.hotbarmanager.listeners.bedwars1058.PlayerKill;
import me.kiiya.hotbarmanager.listeners.bedwars1058.category.RespawnListenerC;
import me.kiiya.hotbarmanager.listeners.bedwars1058.category.ShopBuyC;
import me.kiiya.hotbarmanager.listeners.bedwars1058.ShopOpen;
import me.kiiya.hotbarmanager.listeners.bedwars1058.item.RespawnListenerI;
import me.kiiya.hotbarmanager.listeners.bedwars1058.item.ShopBuyI;
import me.kiiya.hotbarmanager.menu.helpers.CacheManager;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static me.kiiya.hotbarmanager.HotbarManager.*;

public class BedWars1058 {

    public BedWars1058() {
        start();
    }

    private void start() {
        support = Support.BEDWARS1058;
        bw1058Api = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        if (Bukkit.getPluginManager().getPlugin("BedWars1058-Compass") != null) {
            compassAddon = true;
        }

        connectDatabase();
        loadConfig();
        HotbarManager.manager = me.kiiya.hotbarmanager.player.HotbarManager.init();
        if (manager.getSortType() == SortType.ITEM) {
            IShopCacheManager cacheManager = new CacheManager("default", HotbarManager.getVersionSupport());
            cacheManager.loadFromConfig(bw1058Api.getConfigs().getShopConfig().getYml().getConfigurationSection(""));
        }
        loadMessages();
        loadCommands();
        loadListeners();
        isLoaded = true;
    }

    private void connectDatabase() {
        Utility.info("&eConnecting to database...");
        YamlConfiguration config = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getYml();
        if (config.getBoolean("database.enable")) HotbarManager.getInstance().setDB(new MySQL());
        else HotbarManager.getInstance().setDB(new SQLite());

        Utility.info("&aDatabase connected!");
    }

    private void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getInstance(), "config", bw1058Api.getAddonsPath().getPath() + File.separator + "HotbarManager");
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
        Bukkit.getPluginManager().registerEvents(new ShopOpen(), getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerKill(), getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getInstance());
        switch (manager.getSortType()) {
            case CATEGORY:
                Bukkit.getPluginManager().registerEvents(new ShopBuyC(), getInstance());
                Bukkit.getPluginManager().registerEvents(new RespawnListenerC(), getInstance());
                break;
            case ITEM:
                Bukkit.getPluginManager().registerEvents(new ShopBuyI(), getInstance());
                Bukkit.getPluginManager().registerEvents(new RespawnListenerI(), getInstance());
                break;
        }

        if (bw1058Api.getVersionSupport().getVersion() == 0) Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.Legacy(), getInstance());
        else Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.New(), getInstance());

        Utility.info("&aListeners loaded!");
    }
}
