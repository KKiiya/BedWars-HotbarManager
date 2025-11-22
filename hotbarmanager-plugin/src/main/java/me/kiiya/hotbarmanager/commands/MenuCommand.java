package me.kiiya.hotbarmanager.commands;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.menu.HotbarManagerMenu;
import me.kiiya.hotbarmanager.menu.ShopInventoryManager;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.kiiya.hotbarmanager.config.ConfigPaths.NO_PERMISSION;

public class MenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cYou can't use this command from console.");
            return false;
        }

        Player p = (Player) commandSender;
        if (args.length == 0) {
            if (!p.hasPermission("hotbarmanager.menu")) {
                p.sendMessage(Utility.getMsg(p, NO_PERMISSION));
                return false;
            }

            SortType sortType = HotbarManager.getManager().getSortType();
            if (sortType == SortType.CATEGORY) {
                new HotbarManagerMenu(p);
            } else {
                switch (HotbarManager.getSupport()) {
                    case BEDWARSPROXY:
                    case BEDWARS1058:
                    case BEDWARSPROXY2023:
                        new ShopInventoryManager(p, "default");
                        break;
                    case BEDWARS2023:
                        boolean isPlaying = HotbarManager.getBW2023Api().getArenaUtil().isPlaying(p);
                        String group = isPlaying ? HotbarManager.getBW2023Api().getArenaUtil().getArenaByPlayer(p).getGroup() : "default";
                        new ShopInventoryManager(p, group);
                        break;
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!p.hasPermission("hotbarmanager.reload")) {
                    p.sendMessage(Utility.getMsg(p, NO_PERMISSION));
                    return false;
                }

                p.sendMessage("§eReloading HotBarManager...");
                HotbarManager.getMainConfig().reload();
                p.sendMessage("§aHotBarManager has been reloaded.");
            } else {
                p.sendMessage(Utility.getMsg(p, "cmd-not-found").replace("{prefix}", Utility.getMsg(p, "prefix")));
            }
        } else {
            p.sendMessage(Utility.getMsg(p, "cmd-not-found").replace("{prefix}", Utility.getMsg(p, "prefix")));
        }
        return false;
    }
}
