package me.kiiya.hotbarmanager.listeners;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.player.HotbarPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        HotbarManager.getDB().createPlayerData(e.getPlayer());
        new HotbarPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        if (hp == null) return;

        hp.saveHotbar();
        hp.destroy();
    }
}
