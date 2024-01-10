package me.kiiya.hotbarmanager.listeners.mainlisteners;

import me.kiiya.hotbarmanager.HotbarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        HotbarManager.getDB().createPlayerData(e.getPlayer());
    }
}
