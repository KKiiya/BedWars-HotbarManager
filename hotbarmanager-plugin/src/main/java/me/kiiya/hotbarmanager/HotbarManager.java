package me.kiiya.hotbarmanager;

import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarManager;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.commands.MenuCommand;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.support.bedwars1058.BedWars1058;
import me.kiiya.hotbarmanager.support.bedwars1058.BedWarsProxy;
import me.kiiya.hotbarmanager.support.bedwars2023.BedWars2023;
import me.kiiya.hotbarmanager.support.bedwars2023.BedWarsProxy2023;
import me.kiiya.hotbarmanager.support.version.*;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class HotbarManager extends JavaPlugin {

    public static boolean compassAddon = false;

    public static String version;
    public static VersionSupport versionSupport;
    public static me.kiiya.hotbarmanager.api.HotbarManager api;
    public static com.andrei1058.bedwars.api.BedWars bw1058Api;
    public static com.tomkeuper.bedwars.api.BedWars bw2023Api;
    public static MainConfig mainConfig;
    public static Support support;
    public static IHotbarManager manager;
    public static boolean debug = false;
    private Database db;

    @Override
    public void onEnable() {
        if (!loadVersionSupport()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadSupport();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            new Metrics(this, 20334);
            api = new API();

            Bukkit.getServicesManager().register(me.kiiya.hotbarmanager.api.HotbarManager.class, api, this, ServicePriority.Normal);
            getServer().getPluginCommand("hbm").setExecutor(new MenuCommand());
        }, 40L);
    }

    @Override
    public void onDisable() {
        me.kiiya.hotbarmanager.player.HotbarManager.getInstance().saveHotbars(true, false);
    }

    private void loadSupport() {
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") != null) new BedWars1058();
        else if (Bukkit.getPluginManager().getPlugin("BedWars2023") != null) new BedWars2023();
        else if (Bukkit.getPluginManager().getPlugin("BedWarsProxy") != null) new BedWarsProxy();
        else if (Bukkit.getPluginManager().getPlugin("BWProxy2023") != null) new BedWarsProxy2023();
    }

    private boolean loadVersionSupport() {
        version = Bukkit.getBukkitVersion().split("-")[0];
        switch (version) {
            case "1.8.8":
                versionSupport = new v1_8_R3();
                break;
            case "1.12.2":
                versionSupport = new v1_12_R1();
                break;
            case "1.16.5":
                versionSupport = new v1_16_R3();
                break;
            case "1.17.1":
                versionSupport = new v1_17_R1();
                break;
            case "1.18.2":
                versionSupport = new v1_18_R2();
                break;
            case "1.19.3":
                versionSupport = new v1_19_R3();
                break;
            case "1.20.1":
                versionSupport = new v1_20_R1();
                break;
            case "1.20.2":
                versionSupport = new v1_20_R2();
                break;
            case "1.20.3":
                versionSupport = new v1_20_R3();
                break;
            case "1.20.4":
                versionSupport = new v1_20_R4();
                break;
            case "1.21.1":
                versionSupport = new v1_21_R1();
                break;
            case "1.21.8":
                versionSupport = new v1_21_R5();
                break;
            default:
                Utility.info("Unsupported server version: " + version + ". Disabling plugin.");
        }
        return versionSupport != null;
    }

    public static IHotbarManager getManager() {
        return manager;
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    public static VersionSupport getVersionSupport() {
        return versionSupport;
    }

    public static HotbarManager getInstance() {
        return HotbarManager.getPlugin(HotbarManager.class);
    }

    public static me.kiiya.hotbarmanager.api.HotbarManager getAPI() {
        return api;
    }

    public static Support getSupport() {
        return support;
    }

    public static com.tomkeuper.bedwars.api.BedWars getBW2023Api() {
        return bw2023Api;
    }

    public static com.andrei1058.bedwars.api.BedWars getBW1058Api() {
        return bw1058Api;
    }

    public static com.tomkeuper.bedwars.proxy.api.BedWars getBWProxy2023Api() {
        return com.tomkeuper.bedwars.proxy.BedWarsProxy.getAPI();
    }

    public static com.andrei1058.bedwars.proxy.api.BedWars getBWProxyApi() {
        return com.andrei1058.bedwars.proxy.BedWarsProxy.getAPI();
    }

    public static String getVersion() {
        return version;
    }

    public void setDB(Database db) {
        this.db = db;
    }

    public Database getDB() {
        return db;
    }

    public static boolean isCompassAddon() {
        return compassAddon;
    }

}
