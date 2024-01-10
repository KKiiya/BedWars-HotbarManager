package me.kiiya.hotbarmanager.utils;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import org.bukkit.Material;

public class HotbarUtils {
    public static Category getCategoryFromString(String path) {
        String category =  ((path.split("\\.")[0]).split("-")[0]).toUpperCase();
        if (!Category.getCategoriesAsString().contains(category)) return Category.NONE;
        return Category.getFromString(category);
    }

    public static Category getCategoryFromMaterial(Material m) {
        switch (m) {
            case IRON_PICKAXE:
                return Category.TOOLS;
            case HARD_CLAY:
                return Category.BLOCKS;
            case GOLD_SWORD:
                return Category.MELEE;
            case BOW:
                return Category.RANGED;
            case BREWING_STAND_ITEM:
                return Category.POTIONS;
            case TNT:
                return Category.UTILITY;
            case COMPASS:
                return Category.COMPASS;
            default:
                return Category.NONE;
        }
    }

    public static int getSlotForPos(int position) {
        switch (position) {
            case 0:
                return 27;
            case 1:
                return 28;
            case 2:
                return 29;
            case 3:
                return 30;
            case 4:
                return 31;
            case 5:
                return 32;
            case 6:
                return 33;
            case 7:
                return 34;
            case 8:
                return 35;
        }
        return position;
    }

    public static int getPosForSlot(int slot) {
        switch (slot) {
            case 27:
                return 0;
            case 28:
                return 1;
            case 29:
                return 2;
            case 30:
                return 3;
            case 31:
                return 4;
            case 32:
                return 5;
            case 33:
                return 6;
            case 34:
                return 7;
            case 35:
                return 8;
        }
        return slot;
    }
}
