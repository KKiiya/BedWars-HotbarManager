package me.kiiya.hotbarmanager.listeners;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.menu.GUIHolder;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {

    private final VersionSupport vs;

    public InventoryListener() {
        vs = HotbarManager.getVersionSupport();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (p == null) return;
        if (e.getInventory() == null) return;
        if (e.getClickedInventory() == null) return;
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof GUIHolder)) return;

        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            e.setCancelled(true);
            return;
        } else if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
            e.setCancelled(true);
            return;
        }

        if (e.getInventory() != e.getClickedInventory() || e.getClickedInventory() == p.getInventory()) {
            e.getCursor().setType(Material.AIR);
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        ((GUIHolder) e.getInventory().getHolder()).onInventoryClick(e);
    }

    @EventHandler
    public void onInventoryDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (p == null) return;
        if (e.getPlayer().getOpenInventory() == null) return;
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (inv.getHolder() == null) return;

        if (inv.getHolder() instanceof GUIHolder) {
            ((GUIHolder) inv.getHolder()).onInventoryDrop(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        PlayerInventory inv = p.getInventory();

        try {
            for (ItemStack item : inv.getContents()) {
                String tag = vs.getItemTag(item, "hbm");
                if (tag == null) continue;
                inv.remove(item);
            }
        } catch (Exception ignored) {

        }
    }
}
