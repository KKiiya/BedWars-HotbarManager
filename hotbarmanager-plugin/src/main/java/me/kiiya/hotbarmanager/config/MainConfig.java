package me.kiiya.hotbarmanager.config;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.config.ConfigManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;
import static me.kiiya.hotbarmanager.utils.Utility.*;

public class MainConfig extends ConfigManager {
    public MainConfig(Plugin plugin, String name, String dir) {
        super(plugin, name, dir);
        YamlConfiguration yml = getYml();
        yml.options().header("BedWars-HotBarManager v" + HotbarManager.getInstance().getDescription().getVersion() + " By Kiiya.\nDiscord: https://discord.gg/n5yNavRvrP");
        yml.addDefault("debug", false);
        yml.addDefault(ITEM_TYPE, "BLAZE_POWDER");
        yml.addDefault(ITEM_POSITION, 54);

        yml.addDefault(SEPARATOR_MATERIAL, getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "LEGACY_STAINED_GLASS_PANE"));
        yml.addDefault(SEPARATOR_DATA, 7);
        yml.addDefault(SEPARATOR_SKULL_URL, "url-here");
        yml.addDefault(SEPARATOR_POSITIONS, "18,19,20,21,22,23,24,25,26");

        yml.addDefault(PLACEHOLDER_NONE_ENABLED, true);
        yml.addDefault(PLACEHOLDER_NONE_MATERIAL, getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "LEGACY_STAINED_GLASS_PANE"));
        yml.addDefault(PLACEHOLDER_NONE_DATA, 14);
        yml.addDefault(PLACEHOLDER_NONE_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_MELEE_ENABLED, true);
        yml.addDefault(CATEGORY_MELEE_POSITION, 10);
        yml.addDefault(CATEGORY_MELEE_MATERIAL, getForCurrentVersion("GOLD_SWORD", "GOLD_SWORD", "GOLDEN_SWORD"));
        yml.addDefault(CATEGORY_MELEE_DATA, 0);
        yml.addDefault(CATEGORY_MELEE_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_BLOCKS_ENABLED, true);
        yml.addDefault(CATEGORY_BLOCKS_POSITION, 11);
        yml.addDefault(CATEGORY_BLOCKS_MATERIAL, getForCurrentVersion("HARD_CLAY", "HARD_CLAY", "LEGACY_HARD_CLAY"));
        yml.addDefault(CATEGORY_BLOCKS_DATA, 0);
        yml.addDefault(CATEGORY_BLOCKS_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_TOOLS_ENABLED, true);
        yml.addDefault(CATEGORY_TOOLS_POSITION, 12);
        yml.addDefault(CATEGORY_TOOLS_MATERIAL, getForCurrentVersion("IRON_PICKAXE", "IRON_PICKAXE", "IRON_PICKAXE"));
        yml.addDefault(CATEGORY_TOOLS_DATA, 0);
        yml.addDefault(CATEGORY_TOOLS_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_RANGED_ENABLED, true);
        yml.addDefault(CATEGORY_RANGED_POSITION, 13);
        yml.addDefault(CATEGORY_RANGED_MATERIAL, getForCurrentVersion("BOW", "BOW", "BOW"));
        yml.addDefault(CATEGORY_RANGED_DATA, 0);
        yml.addDefault(CATEGORY_RANGED_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_POTIONS_ENABLED, true);
        yml.addDefault(CATEGORY_POTIONS_POSITION, 14);
        yml.addDefault(CATEGORY_POTIONS_MATERIAL, getForCurrentVersion("BREWING_STAND_ITEM", "BREWING_STAND_ITEM", "BREWING_STAND"));
        yml.addDefault(CATEGORY_POTIONS_DATA, 0);
        yml.addDefault(CATEGORY_POTIONS_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_SPECIALS_ENABLED, true);
        yml.addDefault(CATEGORY_SPECIALS_POSITION, 15);
        yml.addDefault(CATEGORY_SPECIALS_MATERIAL, getForCurrentVersion("TNT", "TNT", "TNT"));
        yml.addDefault(CATEGORY_SPECIALS_DATA, 0);
        yml.addDefault(CATEGORY_SPECIALS_SKULL_URL, "url-here");

        yml.addDefault(CATEGORY_COMPASS_ENABLED, true);
        yml.addDefault(CATEGORY_COMPASS_POSITION, 16);
        yml.addDefault(CATEGORY_COMPASS_MATERIAL, getForCurrentVersion("COMPASS", "COMPASS", "COMPASS"));
        yml.addDefault(CATEGORY_COMPASS_DATA, 0);
        yml.addDefault(CATEGORY_COMPASS_SKULL_URL, "url-here");

        yml.addDefault(BACK_MATERIAL, "ARROW");
        yml.addDefault(BACK_DATA, 0);
        yml.addDefault(BACK_SKULL_URL, "url-here");
        yml.addDefault(BACK_POSITION, 48);

        yml.addDefault(HOTBAR_RESET_MATERIAL, "BARRIER");
        yml.addDefault(HOTBAR_RESET_DATA, 0);
        yml.addDefault(HOTBAR_RESET_SKULL_URL, "url-here");
        yml.addDefault(HOTBAR_RESET_POSITION, 50);

        yml.addDefault(HOTBAR_SLOT_0, 27);
        yml.addDefault(HOTBAR_SLOT_1, 28);
        yml.addDefault(HOTBAR_SLOT_2, 29);
        yml.addDefault(HOTBAR_SLOT_3, 30);
        yml.addDefault(HOTBAR_SLOT_4, 31);
        yml.addDefault(HOTBAR_SLOT_5, 32);
        yml.addDefault(HOTBAR_SLOT_6, 33);
        yml.addDefault(HOTBAR_SLOT_7, 34);
        yml.addDefault(HOTBAR_SLOT_8, 35);

        yml.addDefault("enable-compass-support", true);
        yml.addDefault(BACK_COMMAND, "your-command");

        for (int i = 0; i < 9; i++) {
            Category slot = Category.NONE;

            if (i == 0) slot = Category.MELEE;
            else if (i == 8) slot = Category.COMPASS;

            yml.addDefault("default-slots."+i, slot.toString());
        }
        yml.options().copyDefaults(true);
        save();
    }
}
