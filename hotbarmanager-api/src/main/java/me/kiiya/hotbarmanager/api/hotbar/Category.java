package me.kiiya.hotbarmanager.api.hotbar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Category {
    NONE, BLOCKS, MELEE, TOOLS, RANGED, POTIONS, UTILITY, COMPASS;
    public static List<Category> getCategories() {
        return Arrays.asList(BLOCKS, MELEE, TOOLS, RANGED, POTIONS, UTILITY, COMPASS);
    }
    public static List<String> getCategoriesAsString() {
        return Stream.of(BLOCKS, MELEE, TOOLS, RANGED, POTIONS, UTILITY, COMPASS).map(Enum::toString).collect(Collectors.toList());
    }

    public static Category getFromString(String category) {
        switch (category.toUpperCase()) {
            case "BLOCKS":
                return BLOCKS;
            case "MELEE":
                return MELEE;
            case "RANGED":
                return RANGED;
            case "TOOLS":
                return TOOLS;
            case "POTIONS":
                return POTIONS;
            case "UTILITY":
                return UTILITY;
            case "COMPASS":
                return COMPASS;
            case "NONE":
            default:
                return NONE;
        }
    }
}
