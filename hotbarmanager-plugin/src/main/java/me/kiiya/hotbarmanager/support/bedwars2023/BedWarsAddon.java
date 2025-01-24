package me.kiiya.hotbarmanager.support.bedwars2023;

import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.bedwars2023.MessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.database.providers.SQLite;
import me.kiiya.hotbarmanager.listeners.InventoryListener;
import me.kiiya.hotbarmanager.listeners.CustomItemSecurity;
import me.kiiya.hotbarmanager.listeners.bedwars2023.*;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

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
        loadMessages();
        loadCommands();
        loadListeners();
    }

    @Override
    public void unload() {
        Bukkit.getPluginManager().disablePlugin(getPlugin());
    }

    private void connectDatabase() {
        Utility.info("&eConnecting to database...");
        Database db;
        ConfigManager config = HotbarManager.getBW2023Api().getConfigs().getMainConfig();

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
        Bukkit.getPluginManager().registerEvents(new ShopBuy(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new ShopOpen(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerKill(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getInstance());

        if (bw2023Api.getVersionSupport().getVersion() == 0) Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.Legacy(), getInstance());
        else Bukkit.getPluginManager().registerEvents(new CustomItemSecurity.New(), getInstance());

        Utility.info("&aListeners loaded!");
    }
}
