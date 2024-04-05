package me.kiiya.hotbarmanager.listeners.mainlisteners;

import me.kiiya.hotbarmanager.menu.GUIHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (p == null) return;
        if (e.getInventory() == null) return;
        if (e.getClickedInventory() == null) return;
        if (e.getInventory().getHolder() == null) return;

        if (e.getInventory().getHolder() instanceof GUIHolder) {
            if (e.getInventory() != e.getClickedInventory() || e.getClickedInventory() == p.getInventory()) {
                e.getCursor().setType(Material.AIR);
                e.setCancelled(true);
                return;
            }

            e.setCancelled(true);
            ((GUIHolder) e.getInventory().getHolder()).onInventoryClick(e);
        }
    }
}
