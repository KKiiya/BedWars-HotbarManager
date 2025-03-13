package me.kiiya.hotbarmanager.listeners;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.menu.GUIHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
        Inventory clickInv = e.getClickedInventory();

        if (p == null) return;

        PlayerInventory inv = p.getInventory();
        if (e.getAction() == InventoryAction.DROP_ALL_CURSOR || e.getAction() == InventoryAction.DROP_ONE_CURSOR
                || e.getAction() == InventoryAction.DROP_ONE_SLOT || e.getAction() == InventoryAction.DROP_ALL_SLOT) {
            e.setCancelled(true);
            return;
        }

        if (e.getInventory() == null) return;
        if (clickInv == null) return;
        if (clickInv.getHolder() == null) return;

        if ((clickInv.getHolder() instanceof Player && e.getInventory().getHolder() instanceof GUIHolder) ||
                e.getAction() == InventoryAction.DROP_ALL_CURSOR || e.getAction() == InventoryAction.DROP_ONE_CURSOR
                || e.getAction() == InventoryAction.DROP_ONE_SLOT || e.getAction() == InventoryAction.DROP_ALL_SLOT) {
            e.setCancelled(true);

            ItemStack item = e.getCurrentItem();

            if (item != null && item.getType() != Material.AIR) {
                String tag = vs.getItemTag(item, "hbm");
                if (tag != null) {
                    item.setType(Material.AIR);
                }
            }
            return;
        }
        if (!(clickInv.getHolder() instanceof GUIHolder)) return;

        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            e.setCancelled(true);
            return;
        } else if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
            e.setCancelled(true);
            return;
        }

        if (e.getInventory() != clickInv || clickInv == inv) {
            e.setCursor(new ItemStack(Material.AIR));
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        ((GUIHolder) clickInv.getHolder()).onInventoryClick(e);
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        Inventory openInv = p.getOpenInventory().getTopInventory();
        ItemStack oldCursor = e.getOldCursor();
        String tag = vs.getItemTag(oldCursor, "hbm");

        if (oldCursor == null) return;
        if (inv.getHolder() == null) return;
        if (openInv.getHolder() == null) return;
        if (tag == null) return;

        if (inv.getHolder() instanceof GUIHolder && openInv.getHolder() instanceof GUIHolder) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }
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
        ItemStack itemOnCursor = p.getItemOnCursor();

        if (itemOnCursor != null && itemOnCursor.getType() != Material.AIR) {
            String cTag = vs.getItemTag(itemOnCursor, "hbm");
            if (cTag != null && itemOnCursor.getType() != Material.AIR) {
                p.setItemOnCursor(new ItemStack(Material.AIR));
            }
        }

        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            String tag = vs.getItemTag(item, "hbm");
            if (tag == null) continue;
            item.setType(Material.AIR);
        }
    }
}
