package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerKill implements Listener {
    @EventHandler
    public void onPlayerKill(PlayerKillEvent e) {
        if (e.getCause().isFinalKill()) return;
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getVictim());
    }
}
