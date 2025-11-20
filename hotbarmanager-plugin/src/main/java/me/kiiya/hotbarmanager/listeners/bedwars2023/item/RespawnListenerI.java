package me.kiiya.hotbarmanager.listeners.bedwars2023.item;

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class RespawnListenerI implements Listener {
    @EventHandler
    public void onRespawn(PlayerReSpawnEvent e) {
        Player ap = e.getPlayer();
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        List<String> hotbar = p.getHotgarAsStringList();
        VersionSupport vs = HotbarManager.getVersionSupport();

        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
            int slotIndex = 0;
            HashMap<Integer, ItemStack> itemsMap = new HashMap<>();
            for (String path : hotbar) {
                ItemStack item = ap.getInventory().getItem(slotIndex);
                if (item == null || item.getType() == Material.AIR) {
                    slotIndex++;
                    continue;
                }
                String itemPath = vs.getItemTag(item, "tierIdentifier");
                if (itemPath != null && itemPath.equalsIgnoreCase(path)) {
                    itemsMap.put(slotIndex, item);
                }
                slotIndex++;
            }

            for (Integer slot : itemsMap.keySet()) {
                ItemStack item = itemsMap.get(slot);
                String itemPath = vs.getItemTag(item, "tierIdentifier");
                int hotbarIndex = hotbar.indexOf(itemPath);
                if (hotbarIndex != -1) {
                    ap.getInventory().setItem(slot, new ItemStack(Material.AIR));
                    ap.getInventory().setItem(hotbarIndex, item);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onTeamAssign(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) return;
        VersionSupport vs = HotbarManager.getVersionSupport();
        for (Player ap : e.getArena().getPlayers()) {
            IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(ap);
            List<String> hotbar = p.getHotgarAsStringList();

            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
                int slotIndex = 0;
                HashMap<Integer, ItemStack> itemsMap = new HashMap<>();
                for (String path : hotbar) {
                    ItemStack item = ap.getInventory().getItem(slotIndex);
                    if (item == null || item.getType() == Material.AIR) {
                        slotIndex++;
                        continue;
                    }
                    String itemPath = vs.getItemTag(item, "tierIdentifier");
                    if (itemPath != null && itemPath.equalsIgnoreCase(path)) {
                        itemsMap.put(slotIndex, item);
                    }
                    slotIndex++;
                }

                for (Integer slot : itemsMap.keySet()) {
                    ItemStack item = itemsMap.get(slot);
                    String itemPath = vs.getItemTag(item, "tierIdentifier");
                    int hotbarIndex = hotbar.indexOf(itemPath);
                    if (hotbarIndex != -1) {
                        ap.getInventory().setItem(slot, new ItemStack(Material.AIR));
                        ap.getInventory().setItem(hotbarIndex, item);
                    }
                }
            }, 1L);
        }
    }
}
