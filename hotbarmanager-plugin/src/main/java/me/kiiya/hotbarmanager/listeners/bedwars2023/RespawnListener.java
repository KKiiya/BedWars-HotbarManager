package me.kiiya.hotbarmanager.listeners.bedwars2023;

import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RespawnListener implements Listener {
    @EventHandler
    public void onRespawn(PlayerReSpawnEvent e) {
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
    }
}
