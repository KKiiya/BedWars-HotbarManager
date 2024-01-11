package me.kiiya.hotbarmanager.listeners.bedwars2023;

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.gameplay.TeamAssignEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RespawnListener implements Listener {
    @EventHandler
    public void onRespawn(PlayerReSpawnEvent e) {
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        List<Category> hotbar = p.getHotbarAsList();
        ItemStack compass = e.getPlayer().getInventory().getItem(8);
        ItemStack sword = e.getPlayer().getInventory().getItem(0);

        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
            if (hotbar.contains(Category.MELEE)) {
                if (sword == null || sword.getType() == Material.AIR) return;
                int slot = 0;
                for (Category cat : hotbar) {
                    if (cat == Category.MELEE) {
                        e.getPlayer().getInventory().setItem(slot, sword);
                        e.getPlayer().getInventory().setItem(0, new ItemStack(Material.AIR));
                        break;
                    }
                    slot++;
                }
            }
            if (hotbar.contains(Category.COMPASS)) {
                if (compass == null || compass.getType() == Material.AIR) return;
                int slot = 0;
                for (Category cat : hotbar) {
                    if (cat == Category.COMPASS) {
                        e.getPlayer().getInventory().setItem(slot, compass);
                        e.getPlayer().getInventory().setItem(8, new ItemStack(Material.AIR));
                        break;
                    }
                    slot++;
                }
            } else {
                e.getPlayer().getInventory().setItem(8, new ItemStack(Material.AIR));
                if (compass != null && compass.getType() != Material.AIR) e.getPlayer().getInventory().setItem(17, compass);
            }
        }, 1L);
    }

    @EventHandler
    public void onTeamAssign(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) return;
        for (Player ap : e.getArena().getPlayers()) {
            IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(ap);
            List<Category> hotbar = p.getHotbarAsList();
            ItemStack compass = ap.getInventory().getItem(8);
            ItemStack sword = ap.getInventory().getItem(0);

            Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                if (hotbar.contains(Category.MELEE)) {
                    if (sword == null || sword.getType() == Material.AIR) return;
                    int slot = 0;
                    for (Category cat : hotbar) {
                        if (cat == Category.MELEE) {
                            ap.getInventory().setItem(0, new ItemStack(Material.AIR));
                            ap.getInventory().setItem(slot, sword);
                            break;
                        }
                        slot++;
                    }
                }
                if (hotbar.contains(Category.COMPASS)) {
                    if (compass == null || compass.getType() == Material.AIR) return;
                    int slot = 0;
                    for (Category cat : hotbar) {
                        if (cat == Category.COMPASS) {
                            ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                            ap.getInventory().setItem(slot, compass);
                            break;
                        }
                        slot++;
                    }
                } else {
                    ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                    if (compass != null && compass.getType() != Material.AIR) ap.getInventory().setItem(17, compass);
                }
            }, 1L);
        }
    }
}
