package me.kiiya.hotbarmanager.support.bedwars2023;

import com.tomkeuper.bedwars.api.addon.Addon;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.config.bedwars2023.MessagesData;
import me.kiiya.hotbarmanager.database.providers.MySQL;
import me.kiiya.hotbarmanager.database.providers.SQLite;
import me.kiiya.hotbarmanager.listeners.bedwars2023.*;
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
        return HotbarManager.getPlugins();
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
        if (Bukkit.getPluginManager().getPlugin("BedWars1058-Compass") != null) {
            compassAddon = true;
        }

        connectDatabase();
        loadConfig();
        loadMessages();
        loadCommands();
        loadListeners();
    }

    @Override
    public void unload() {
        Bukkit.getPluginManager().disablePlugin(getPlugin());
    }

    public void connectDatabase() {
        Utility.info("&eConnecting to database...");
        if (HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.type").equalsIgnoreCase("mysql")) {
            db = new MySQL();
        } else {
            db = new SQLite();
        }
        Utility.info("&aDatabase connected!");
    }

    public void loadConfig() {
        Utility.info("&eLoading config...");
        mainConfig = new MainConfig(HotbarManager.getPlugins(), "config", bw2023Api.getAddonsPath().getPath() + File.separator + "HotbarManager");
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
        Bukkit.getPluginManager().registerEvents(new ShopBuy(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new ShopOpen(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerKill(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), getPlugin());
        Utility.info("&aListeners loaded!");
    }
}
