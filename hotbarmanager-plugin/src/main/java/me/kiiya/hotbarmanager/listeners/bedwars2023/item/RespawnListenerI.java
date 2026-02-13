package me.kiiya.hotbarmanager.listeners.bedwars2023.item;

import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.kiiya.hotbarmanager.utils.Utility.debug;

public class RespawnListenerI implements Listener {
    @EventHandler
    public void onRespawn(PlayerReSpawnEvent e) {
        Player ap = e.getPlayer();
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        List<String> hotbar = p.getHotgarAsStringList();
        VersionSupport vs = HotbarManager.getVersionSupport();

        debug("===== RESPAWN SORTING START for " + ap.getName() + " =====");
        debug("Expected hotbar order: " + hotbar);

        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
            // --- COMPASS HANDLING (Special case) ---
            ItemStack compass = ap.getInventory().getItem(8);
            boolean compassInHotbar = hotbar.contains("compass");
            debug("Compass check: " + (compass != null ? compass.getType() : "null") + ", in hotbar: " + compassInHotbar);
            
            if (compass != null && compass.getType() != Material.AIR) {
                // Always clear slot 8 first to prevent duplication
                ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                
                if (compassInHotbar) {
                    // Compass is configured in hotbar, will be placed later in correct slot
                    debug("Compass will be placed in configured slot");
                } else {
                    // Compass NOT in hotbar config - move to inventory (exclusion)
                    if (HotbarManager.isCompassAddon()) {
                        debug("Compass excluded from hotbar, moving to inventory slot 17");
                        ap.getInventory().setItem(17, compass);
                        compass = null; // Prevent placing in hotbar later
                    }
                }
            }

            debug("--- Phase 1: Scanning ALL inventory slots ---");
            HashMap<String, ItemStack> foundItems = new HashMap<>();
            List<ItemStack> itemsToAddBack = new ArrayList<>();

            // First pass: Find all items with identifiers
            for (int i = 0; i < 9; i++) {
                ItemStack item = ap.getInventory().getItem(i);

                if (item == null || item.getType() == Material.AIR) {
                    debug("Slot " + i + " is empty");
                    continue;
                }

                String identifier = vs.getItemTag(item, "tierIdentifier");
                if (identifier == null) {
                    debug("Slot " + i + " has " + item.getType() + " but no identifier, skipping");
                    itemsToAddBack.add(item);
                    continue;
                }

                String category = HotbarUtils.getCategoryFromString(identifier).toString().toLowerCase();
                String itemPath = category + "." + identifier.split("\\.")[2];

                debug("Slot " + i + " has " + item.getType() + " with path: " + itemPath);

                // Check if this item belongs ANYWHERE in the hotbar
                if (hotbar.contains(itemPath)) {
                    debug("  -> Item belongs in hotbar, storing it");
                    foundItems.put(itemPath, item);
                } else {
                    debug("  -> Item NOT in expected hotbar, adding back...");
                    itemsToAddBack.add(item);
                }
            }

            debug("--- Phase 2: Found items ---");
            debug("Found items count: " + foundItems.size());
            for (String path : foundItems.keySet()) {
                debug("  -> " + path + ": " + foundItems.get(path).getType());
            }

            debug("--- Phase 3: Clearing hotbar and placing items in correct positions ---");
            // Clear hotbar first
            for (int i = 0; i < 9; i++) {
                ap.getInventory().setItem(i, new ItemStack(Material.AIR));
            }

            // Place items in their correct positions
            for (int i = 0; i < hotbar.size(); i++) {
                String expectedPath = hotbar.get(i);

                // Special handling for compass
                if (expectedPath.equalsIgnoreCase("compass")) {
                    if (compass != null && compass.getType() != Material.AIR) {
                        debug("Placing compass in slot " + i);
                        ap.getInventory().setItem(i, compass);
                    } else {
                        debug("Slot " + i + " expects compass but compass not found");
                    }
                    continue;
                }

                if (expectedPath.equalsIgnoreCase("none") || expectedPath.equalsIgnoreCase("MELEE")) {
                    debug("Slot " + i + " expects '" + expectedPath + "', skipping");
                    continue;
                }

                if (foundItems.containsKey(expectedPath)) {
                    ItemStack item = foundItems.get(expectedPath);
                    debug("Placing " + item.getType() + " (" + expectedPath + ") in slot " + i);
                    ap.getInventory().setItem(i, item);
                } else {
                    debug("Slot " + i + " expects '" + expectedPath + "' but item not found");
                }
            }

            for (ItemStack item : itemsToAddBack) {
                debug("Adding back non-hotbar item: " + item.getType());
                ap.getInventory().addItem(item);
            }

            debug("--- Phase 4: Final inventory state ---");
            for (int i = 0; i < 9; i++) {
                ItemStack finalItem = ap.getInventory().getItem(i);
                String finalType = (finalItem == null || finalItem.getType() == Material.AIR) ? "EMPTY" : finalItem.getType().toString();
                String finalId = (finalItem != null) ? vs.getItemTag(finalItem, "tierIdentifier") : null;
                debug("Final slot " + i + ": " + finalType + " (ID: " + finalId + ") | Expected: " + hotbar.get(i));
            }
            debug("===== RESPAWN SORTING END =====");
        }, 1L);
    }

    @EventHandler
    public void onTeamAssign(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) return;
        VersionSupport vs = HotbarManager.getVersionSupport();

        for (Player ap : e.getArena().getPlayers()) {
            IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(ap);
            List<String> hotbar = p.getHotgarAsStringList();

            debug("===== RESPAWN SORTING START for " + ap.getName() + " =====");
            debug("Expected hotbar order: " + hotbar);

            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
                // --- COMPASS HANDLING (Special case) ---
                ItemStack compass = ap.getInventory().getItem(8);
                boolean compassInHotbar = hotbar.contains("compass");
                debug("Compass check: " + (compass != null ? compass.getType() : "null") + ", in hotbar: " + compassInHotbar);
                
                if (compass != null && compass.getType() != Material.AIR) {
                    // Always clear slot 8 first to prevent duplication
                    ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                    
                    if (compassInHotbar) {
                        // Compass is configured in hotbar, will be placed later in correct slot
                        debug("Compass will be placed in configured slot");
                    } else {
                        // Compass NOT in hotbar config - move to inventory (exclusion)
                        if (HotbarManager.isCompassAddon()) {
                            debug("Compass excluded from hotbar, moving to inventory slot 17");
                            ap.getInventory().setItem(17, compass);
                            compass = null; // Prevent placing in hotbar later
                        }
                    }
                }

                debug("--- Phase 1: Scanning ALL inventory slots ---");
                HashMap<String, ItemStack> foundItems = new HashMap<>();
                List<ItemStack> itemsToAddBack = new ArrayList<>();

                // First pass: Find all items with identifiers
                for (int i = 0; i < 9; i++) {
                    ItemStack item = ap.getInventory().getItem(i);

                    if (item == null || item.getType() == Material.AIR) {
                        debug("Slot " + i + " is empty");
                        continue;
                    }

                    String identifier = vs.getItemTag(item, "tierIdentifier");
                    if (identifier == null) {
                        debug("Slot " + i + " has " + item.getType() + " but no identifier, skipping");
                        itemsToAddBack.add(item);
                        continue;
                    }

                    String category = HotbarUtils.getCategoryFromString(identifier).toString().toLowerCase();
                    String itemPath = category + "." + identifier.split("\\.")[2];

                    debug("Slot " + i + " has " + item.getType() + " with path: " + itemPath);

                    // Check if this item belongs ANYWHERE in the hotbar
                    if (hotbar.contains(itemPath)) {
                        debug("  -> Item belongs in hotbar, storing it");
                        foundItems.put(itemPath, item);
                    } else {
                        debug("  -> Item NOT in expected hotbar, will add back");
                        itemsToAddBack.add(item);
                    }
                }

                debug("--- Phase 2: Found items ---");
                debug("Found items count: " + foundItems.size());
                for (String path : foundItems.keySet()) {
                    debug("  -> " + path + ": " + foundItems.get(path).getType());
                }

                debug("--- Phase 3: Clearing hotbar and placing items in correct positions ---");
                // Clear hotbar first
                for (int i = 0; i < 9; i++) {
                    ap.getInventory().setItem(i, new ItemStack(Material.AIR));
                }

                // Place items in their correct positions
                for (int i = 0; i < hotbar.size(); i++) {
                    String expectedPath = hotbar.get(i);

                    // Special handling for compass
                    if (expectedPath.equalsIgnoreCase("compass")) {
                        if (compass != null && compass.getType() != Material.AIR) {
                            debug("Placing compass in slot " + i);
                            ap.getInventory().setItem(i, compass);
                        } else {
                            debug("Slot " + i + " expects compass but compass not found");
                        }
                        continue;
                    }

                    if (expectedPath.equalsIgnoreCase("none") || expectedPath.equalsIgnoreCase("MELEE")) {
                        debug("Slot " + i + " expects '" + expectedPath + "', skipping");
                        continue;
                    }

                    if (foundItems.containsKey(expectedPath)) {
                        ItemStack item = foundItems.get(expectedPath);
                        debug("Placing " + item.getType() + " (" + expectedPath + ") in slot " + i);
                        ap.getInventory().setItem(i, item);
                    } else {
                        debug("Slot " + i + " expects '" + expectedPath + "' but item not found");
                    }
                }

                for (ItemStack item : itemsToAddBack) {
                    debug("Adding back non-hotbar item: " + item.getType());
                    ap.getInventory().addItem(item);
                }

                debug("--- Phase 4: Final inventory state ---");
                for (int i = 0; i < 9; i++) {
                    ItemStack finalItem = ap.getInventory().getItem(i);
                    String finalType = (finalItem == null || finalItem.getType() == Material.AIR) ? "EMPTY" : finalItem.getType().toString();
                    String finalId = (finalItem != null) ? vs.getItemTag(finalItem, "tierIdentifier") : null;
                    debug("Final slot " + i + ": " + finalType + " (ID: " + finalId + ") | Expected: " + hotbar.get(i));
                }
                debug("===== RESPAWN SORTING END =====");
            }, 1L);
        }
    }
}