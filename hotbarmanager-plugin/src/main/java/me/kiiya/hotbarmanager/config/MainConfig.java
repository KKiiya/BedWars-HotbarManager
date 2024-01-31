package me.kiiya.hotbarmanager.config;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class MainConfig extends ConfigManager {
    public MainConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);
        YamlConfiguration yml = getYml();
        yml.options().header("BedWars-HotBarManager v" + HotbarManager.getPlugins().getDescription().getVersion() + " By Kiiya.\nDiscord: https://discord.gg/n5yNavRvrP");
        yml.addDefault(ITEM_TYPE, "BLAZE_POWDER");
        yml.addDefault(ITEM_POSITION, 54);
        yml.addDefault(ENABLE_BLOCKS_CATEGORY, true);
        yml.addDefault(ENABLE_MELEE_CATEGORY, true);
        yml.addDefault(ENABLE_TOOLS_CATEGORY, true);
        yml.addDefault(ENABLE_RANGED_CATEGORY, true);
        yml.addDefault(ENABLE_POTIONS_CATEGORY, true);
        yml.addDefault(ENABLE_SPECIALS_CATEGORY, true);
        yml.addDefault("enable-compass-support", true);
        yml.addDefault(BACK_COMMAND, "your-command");
        yml.options().copyDefaults(true);
        save();
    }
}
