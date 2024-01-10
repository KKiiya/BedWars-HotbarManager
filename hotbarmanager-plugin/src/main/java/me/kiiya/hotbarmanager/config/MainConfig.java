package me.kiiya.hotbarmanager.config;

import me.kiiya.hotbarmanager.api.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class MainConfig extends ConfigManager {
    public MainConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);
        YamlConfiguration yml = getYml();
        yml.options().header("BedWars-HotBarManager v1.0 By Kiiya.\nDiscord: https://discord.gg/n5yNavRvrP");
        yml.addDefault(ITEM_TYPE, "BLAZE_POWDER");
        yml.addDefault(ITEM_POSITION, 54);
        yml.addDefault("enable-compass-support", true);
        yml.addDefault(BACK_COMMAND, "your-command");
        yml.options().copyDefaults(true);
        save();
    }
}
