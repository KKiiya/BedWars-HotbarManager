package me.kiiya.hotbarmanager.config.bedwars1058;

import com.andrei1058.bedwars.api.language.Language;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Arrays;
import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class MessagesData {
    public MessagesData() {
        setup();
    }

    private void setup() {
        for (Language l : Language.getLanguages()) {
            YamlConfiguration yml = l.getYml();
            switch (l.getIso()) {
                default:
                    yml.addDefault(NO_PERMISSION, "&cYou do not have permission to use this command!");
                    yml.addDefault(INVENTORY_NAME, "HotBar Manager");
                    yml.addDefault(INVENTORY_ITEM_NAME, "&aHotbar Manager");
                    yml.addDefault(INVENTORY_ITEM_LORE, Arrays.asList("&7Edit preferred slots for", "&7any item in the shop.", "", "&eClick to edit!"));
                    yml.addDefault(INVENTORY_ITEMS_BLOCKS_NAME, "&aBlocks");
                    yml.addDefault(INVENTORY_ITEMS_MELEE_NAME, "&aMelee");
                    yml.addDefault(INVENTORY_ITEMS_TOOLS_NAME, "&aTools");
                    yml.addDefault(INVENTORY_ITEMS_RANGED_NAME, "&aRanged");
                    yml.addDefault(INVENTORY_ITEMS_POTIONS_NAME, "&aPotions");
                    yml.addDefault(INVENTORY_ITEMS_SPECIALS_NAME, "&aUtility");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_NAME, "&aCompass");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_LORE, Arrays.asList("&7Drag this to the slot your", "&7compass will be set on spawn.", "", "&cIf no slot has a compass, you", "&cwill not be given one.", "", "&eClick to drag!"));
                    yml.addDefault(INVENTORY_ITEMS_LORE, Arrays.asList("&7Drag this to a hotbar slot below", "&7to favor that slot when", "&7purchasing an item in this", "&7category or on spawn.", "", "&eClick to drag!"));
                    yml.addDefault(INVENTORY_ITEMS_USED_LORE, Arrays.asList("&7{category} &7items will prioritize this", "&7slot!", "", "&eClick to remove!"));
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_NAME, "&aGo Back");
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_LORE, Arrays.asList("&7To Play BedWars"));
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_NAME, "&aGo Back");
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_LORE, Arrays.asList("&7To Quick Buy."));
                    yml.addDefault(INVENTORY_ITEMS_RESET_NAME, "&cReset to Default");
                    yml.addDefault(INVENTORY_ITEMS_RESET_LORE, Arrays.asList("&7Reset your hotbar to the", "&7default."));
                    yml.addDefault(MEANING_BLOCKS, "&7Blocks");
                    yml.addDefault(MEANING_MELEE, "&7Melee");
                    yml.addDefault(MEANING_TOOLS, "&7Tools");
                    yml.addDefault(MEANING_RANGED, "&7Ranged");
                    yml.addDefault(MEANING_POTIONS, "&7Potions");
                    yml.addDefault(MEANING_SPECIALS, "&7Specials");
                    yml.addDefault(MEANING_COMPASS, "&7Compass");
                    yml.addDefault(SEPARATOR_NAME, "&7↑ Categories");
                    yml.addDefault(SEPARATOR_LORE, Arrays.asList("&7↓ Hotbar"));
                    break;
            }
            yml.options().copyDefaults(true);
            l.save();
        }
    }
}
