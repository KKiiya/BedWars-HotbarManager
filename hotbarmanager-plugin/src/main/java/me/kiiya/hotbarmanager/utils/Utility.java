package me.kiiya.hotbarmanager.utils;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
    public static void info(String text) {
        Bukkit.getConsoleSender().sendMessage("[" + HotbarManager.getInstance().getName() + "] " + c(text));
    }

    public static void warn(String text) {
        HotbarManager.getInstance().getLogger().warning(c(text));
    }

    public static String c(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String p(Player player, String text) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return c(text);
        return c(PlaceholderAPI.setPlaceholders(player, text));
    }

    public static String getMsg(Player player, String path) {
        if (HotbarManager.getSupport() == Support.BEDWARS1058) return p(player, HotbarManager.getBW1058Api().getPlayerLanguage(player).getString(path));
        else if (HotbarManager.getSupport() == Support.BEDWARS2023) return p(player, HotbarManager.getBW2023Api().getPlayerLanguage(player).getString(path));
        else if (HotbarManager.getSupport() == Support.BEDWARSPROXY) return p(player, HotbarManager.getBWProxyApi().getLanguageUtil().getMsg(player, path));
        else if (HotbarManager.getSupport() == Support.BEDWARSPROXY2023) return p(player, HotbarManager.getBWProxy2023Api().getLanguageUtil().getMsg(player, path));
        else return c("&cMISSING");
    }

    public static List<String> getListMsg(Player player, String path) {
        if (HotbarManager.getSupport() == Support.BEDWARS1058) return HotbarManager.getBW1058Api().getPlayerLanguage(player).getList(path).stream().map(s -> p(player, s)).collect(Collectors.toList());
        else if (HotbarManager.getSupport() == Support.BEDWARS2023) return HotbarManager.getBW2023Api().getPlayerLanguage(player).getList(path).stream().map(s -> p(player, s)).collect(Collectors.toList());
        else if (HotbarManager.getSupport() == Support.BEDWARSPROXY) return HotbarManager.getBWProxyApi().getLanguageUtil().getList(player, path).stream().map(s -> p(player, s)).collect(Collectors.toList());
        else if (HotbarManager.getSupport() == Support.BEDWARSPROXY2023) return HotbarManager.getBWProxy2023Api().getLanguageUtil().getList(player, path).stream().map(s -> p(player, s)).collect(Collectors.toList());
        else return Collections.singletonList(c("&cMISSING"));
    }

    public static boolean isTool(ItemStack itemStack) {
        switch (HotbarManager.getSupport()) {
            case BEDWARS2023:
                return HotbarManager.getBW2023Api().getVersionSupport().isTool(itemStack);
            case BEDWARS1058:
                return HotbarManager.getBW1058Api().getVersionSupport().isTool(itemStack);
            default:
                return false;
        }
    }

    public static boolean isItemHigherTier(ItemStack item1, ItemStack item2) {
        return isMaterialHigherTier(item1.getType(), item2.getType());
    }

    public static boolean isMaterialHigherTier(Material material1, Material material2) {
        String[] materialTiers = {"WOOD", "STONE", "IRON", "GOLD", "DIAMOND", "NETHERITE"};

        String mineral1 = material1.toString().split("_")[0];
        String mineral2 = material2.toString().split("_")[0];

        int tier1 = -1, tier2 = -1;

        for (int i = 0; i < materialTiers.length; i++) {
            if (mineral1.contains(materialTiers[i])) tier1 = i;
            if (mineral2.contains(materialTiers[i])) tier2 = i;
        }

        if (tier1 == -1 || tier2 == -1) return false;
        return tier1 > tier2;
    }


    public static ItemStack formatItemStack(ItemStack item, Object t) {
        ItemStack cloneItem = item.clone();
        if (HotbarManager.getSupport() == Support.BEDWARS1058) {
            ITeam team = (ITeam) t;
            cloneItem = BedWars.nms.colourItem(cloneItem, team);
        } else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
            com.tomkeuper.bedwars.api.arena.team.ITeam team = (com.tomkeuper.bedwars.api.arena.team.ITeam) t;
            cloneItem = com.tomkeuper.bedwars.BedWars.nms.colourItem(cloneItem, team);
        }
        return cloneItem;
    }

    public static Category getItemCategory(ItemStack item) {
        Material type = item.getType();
        switch (HotbarManager.getSupport()) {
            case BEDWARS1058:
                if (BedWars.nms.isTool(item)) return Category.TOOLS;
                if (BedWars.nms.isBow(item)) return Category.RANGED;
                if (BedWars.nms.isSword(item) || type == Material.STICK) return Category.MELEE;

                if (type == BedWars.nms.materialSnowball()) return Category.UTILITY;
                if (type == BedWars.nms.materialFireball()) return Category.UTILITY;
                if (type == Material.TNT) return Category.UTILITY;
                if (type == Material.ENDER_PEARL) return Category.UTILITY;
                if (type == Material.EGG) return Category.UTILITY;
                if (type.toString().contains("SPONGE")) return Category.UTILITY;

                if (item.getType().isBlock()) return Category.BLOCKS;
                break;
            case BEDWARS2023:
                if (com.tomkeuper.bedwars.BedWars.nms.isTool(item)) return Category.TOOLS;
                if (com.tomkeuper.bedwars.BedWars.nms.isBow(item)) return Category.RANGED;
                if (com.tomkeuper.bedwars.BedWars.nms.isSword(item) || type == Material.STICK) return Category.MELEE;

                if (type == com.tomkeuper.bedwars.BedWars.nms.materialSnowball()) return Category.UTILITY;
                if (type == com.tomkeuper.bedwars.BedWars.nms.materialFireball()) return Category.UTILITY;
                if (type == Material.TNT) return Category.UTILITY;
                if (type == Material.ENDER_PEARL) return Category.UTILITY;
                if (type == Material.EGG) return Category.UTILITY;
                if (type.toString().contains("SPONGE")) return Category.UTILITY;

                if (item.getType().isBlock()) return Category.BLOCKS;
                break;
        }
        return Category.NONE;
    }

    public static ItemStack setItemTag(ItemStack item, String tag, String value) {
        switch (HotbarManager.getSupport()) {
            case BEDWARS2023:
                return HotbarManager.getBW2023Api().getVersionSupport().setTag(item, tag, value);
            case BEDWARS1058:
                return HotbarManager.getBW1058Api().getVersionSupport().setTag(item, tag, value);
            case BEDWARSPROXY2023:
            case BEDWARSPROXY:
            default:
                return item;
        }
    }

    public static String getTag(ItemStack item, String tag) {
        switch (HotbarManager.getSupport()) {
            case BEDWARS2023:
                return HotbarManager.getBW2023Api().getVersionSupport().getTag(item, tag);
            case BEDWARS1058:
                return HotbarManager.getBW1058Api().getVersionSupport().getTag(item, tag);
            case BEDWARSPROXY2023:
            case BEDWARSPROXY:
            default:
                return null;
        }
    }
}
