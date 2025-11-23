package me.kiiya.hotbarmanager.listeners;

import me.kiiya.hotbarmanager.HotbarManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!HotbarManager.isLoaded) {
            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
                e.getPlayer().kickPlayer("Server is still loading, please try again in a few seconds.");
            }, 5L);
        }
    }
}
