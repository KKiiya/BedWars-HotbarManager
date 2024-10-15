package me.kiiya.hotbarmanager.listeners;

import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemSecurity {

    public static class Legacy implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPickUp(PlayerPickupItemEvent e) {
            Item item = e.getItem();
            ItemStack itemStack = item.getItemStack();
            String tag = Utility.getTag(itemStack, "hbm");

            if (tag != null) {
                e.setCancelled(true);
                item.remove();
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onDrop(PlayerDropItemEvent e) {
            Item item = e.getItemDrop();
            ItemStack itemStack = item.getItemStack();
            String tag = Utility.getTag(itemStack, "hbm");

            if (tag != null) {
                item.remove();
                e.setCancelled(true);
                itemStack.setType(Material.AIR);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onItemUse(PlayerInteractEvent e) {
            ItemStack itemStack = e.getItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            String tag = Utility.getTag(itemStack, "hbm");
            if (tag == null) return;

            e.setCancelled(true);
            itemStack.setType(Material.AIR);
        }
    }

    public static class New implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPickUp(EntityPickupItemEvent e) {
            Item item = e.getItem();
            ItemStack itemStack = item.getItemStack();
            String tag = Utility.getTag(itemStack, "hbm");

            if (tag != null) {
                e.setCancelled(true);
                item.remove();
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onDrop(PlayerDropItemEvent e) {
            Item item = e.getItemDrop();
            ItemStack itemStack = item.getItemStack();
            String tag = Utility.getTag(itemStack, "hbm");

            if (tag != null) {
                item.remove();
                e.setCancelled(true);
                itemStack.setType(Material.AIR);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onItemUse(PlayerInteractEvent e) {
            ItemStack itemStack = e.getItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            String tag = Utility.getTag(itemStack, "hbm");
            if (tag == null) return;

            e.setCancelled(true);
            itemStack.setType(Material.AIR);
        }
    }
}
