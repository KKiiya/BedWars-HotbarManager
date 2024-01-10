package me.kiiya.hotbarmanager.listeners.bedwars1058;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;
import static me.kiiya.hotbarmanager.config.ConfigPaths.ITEM_POSITION;

public class ShopOpen implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (HotbarManager.getSupport() != Support.BEDWARS2023) return;

        Player player = (Player) e.getPlayer();
        if (!HotbarManager.getBW2023Api().getArenaUtil().isPlaying(player)) return;

        if (e.getView().getTitle().equals(Utility.getMsg(player, "shop-items-messages.inventory-name"))) {
            ItemStack hotbarManagerItem = new ItemStack(Material.valueOf(HotbarManager.getMainConfig().getString(ITEM_TYPE)));
            ItemMeta hotbarManagerItemMeta = hotbarManagerItem.getItemMeta();
            hotbarManagerItemMeta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEM_NAME));
            hotbarManagerItemMeta.setLore(Utility.getListMsg(player, INVENTORY_ITEM_LORE));
            hotbarManagerItem.setItemMeta(hotbarManagerItemMeta);
            e.getInventory().setItem(HotbarManager.getMainConfig().getInt(ITEM_POSITION) - 1, hotbarManagerItem);
        }
    }

    @EventHandler
    public void onHotbarOpen(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (p == null) return;
        if (e.getInventory() == null) return;
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (!HotbarManager.getBW1058Api().getArenaUtil().isPlaying(p)) return;

        if (e.getView().getTitle().equals(Utility.getMsg(p, "shop-items-messages.inventory-name"))) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(Utility.getMsg(p, INVENTORY_ITEM_NAME))) {
                Bukkit.getServer().dispatchCommand(p, "hbm");
            }
        }
    }
}
