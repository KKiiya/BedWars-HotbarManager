package me.kiiya.hotbarmanager.commands;

import me.kiiya.hotbarmanager.menu.HotbarManagerMenu;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.kiiya.hotbarmanager.config.ConfigPaths.NO_PERMISSION;

public class MenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("§cYou can't use this command from console.");
            return false;
        }

        Player p = (Player) commandSender;
        if (!p.hasPermission("hotbarmanager.menu")) {
            p.sendMessage(Utility.getMsg(p, NO_PERMISSION));
            return false;
        }

        new HotbarManagerMenu(p);
        return false;
    }
}
