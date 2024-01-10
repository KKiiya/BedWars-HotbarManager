package me.kiiya.hotbarmanager.utils;

import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamColor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
    public static void info(String text) {
        HotbarManager.getPlugins().getLogger().info(c(text));
    }
    public static void warn(String text) {
        HotbarManager.getPlugins().getLogger().warning(c(text));
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
        else return Arrays.asList(c("&cMISSING"));
    }

    public static ItemStack formatItemStack(ItemStack item, Object t) {
        if (item.getType() != Material.WOOL) return item;
        if (HotbarManager.getSupport() == Support.BEDWARS1058) {
            ITeam team = (ITeam) t;
            if (team == null) return item;
            byte color = team.getColor().itemByte();
            if (team.getColor() == TeamColor.AQUA) color = 9;
            item.setDurability(color);
        }
        else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
            com.tomkeuper.bedwars.api.arena.team.ITeam team = (com.tomkeuper.bedwars.api.arena.team.ITeam) t;
            if (team == null) return item;
            byte color = team.getColor().itemByte();
            if (team.getColor() == com.tomkeuper.bedwars.api.arena.team.TeamColor.AQUA) color = 9;
            item.setDurability(color);
        }
        return item;
    }

    public static void playSound(Player p, String sound, float volume, float pitch) {
       p.playSound(p.getLocation(), Sound.valueOf(sound), volume, pitch);
    }

    public static Category getItemCategory(ItemStack item) {
        Material type = item.getType();
        if (type == Material.TNT) return Category.UTILITY;
        else if (type == Material.POTION) return Category.POTIONS;
        else if (type == Material.COMPASS) return Category.COMPASS;
        else {
            if (type.isBlock()) return Category.BLOCKS;
            else if (type.isEdible()) return Category.UTILITY;
            else if (type.toString().contains("_SWORD") || type.toString().contains("STICK")) return Category.MELEE;
            else if (type.toString().contains("_AXE") || type.toString().contains("_PICKAXE") || type.toString().contains("SHEARS")) return Category.TOOLS;
        }
        return Category.NONE;
    }
}
