package me.kiiya.hotbarmanager.listeners.bedwars1058.category;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.player.PlayerReSpawnEvent;
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

public class RespawnListenerC implements Listener {
    @EventHandler
    public void onRespawn(PlayerReSpawnEvent e) {
        Player ap = e.getPlayer();
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        List<Category> hotbar = p.getHotbarAsList();

        ItemStack sword = e.getPlayer().getInventory().getItem(0);
        ItemStack compass = e.getPlayer().getInventory().getItem(8);

        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
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
                if (HotbarManager.isCompassAddon()) {
                    ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                    if (compass != null && compass.getType() != Material.AIR) ap.getInventory().setItem(17, compass);
                }
            }

            if (hotbar.contains(Category.TOOLS)) {
                for (int i = 0; i < 9; i++) {
                    ItemStack currentItem = ap.getInventory().getItem(i);

                    if (currentItem != null && currentItem.getType() != Material.AIR && (BedWars.nms.isTool(currentItem) || currentItem.getType() == Material.SHEARS)) {
                        int availableSlot = -1;

                        for (int j = 0; j < 9; j++) {
                            if (hotbar.get(j) == Category.TOOLS && ap.getInventory().getItem(j) == null) {
                                availableSlot = j;
                                break;
                            }
                        }

                        if (availableSlot != -1) {
                            ap.getInventory().setItem(i, new ItemStack(Material.AIR));
                            ap.getInventory().setItem(availableSlot, currentItem);
                        }
                    }
                }
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

            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
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
                    if (HotbarManager.isCompassAddon()) {
                        ap.getInventory().setItem(8, new ItemStack(Material.AIR));
                        if (compass != null && compass.getType() != Material.AIR) ap.getInventory().setItem(17, compass);
                    }
                }

                if (hotbar.contains(Category.TOOLS)) {
                    ItemStack tool1 = ap.getInventory().getItem(1);
                    ItemStack tool2 = ap.getInventory().getItem(2);
                    ItemStack tool3 = ap.getInventory().getItem(3);

                    int slot = 0;
                    if (tool1 != null && tool1.getType() != Material.AIR) {
                        for (Category cat : hotbar) {
                            if (cat == Category.TOOLS) {
                                ap.getInventory().setItem(1, new ItemStack(Material.AIR));
                                ap.getInventory().setItem(slot, tool1);
                                break;
                            }
                            slot++;
                        }
                    }

                    int slot1 = 0;
                    if (tool2 != null && tool2.getType() != Material.AIR) {
                        for (Category cat : hotbar) {
                            if (cat == Category.TOOLS) {
                                if (ap.getInventory().getItem(slot1) == null || ap.getInventory().getItem(slot1).getType() == Material.AIR) {
                                    ap.getInventory().setItem(2, new ItemStack(Material.AIR));
                                    ap.getInventory().setItem(slot1, tool2);
                                } else continue;
                                break;
                            }
                            slot1++;
                        }
                    }

                    int slot2 = 0;
                    if (tool3 != null && tool3.getType() != Material.AIR) {
                        for (Category cat : hotbar) {
                            if (cat == Category.TOOLS) {
                                if (ap.getInventory().getItem(slot2) == null || ap.getInventory().getItem(slot2).getType() == Material.AIR) {
                                    ap.getInventory().setItem(3, new ItemStack(Material.AIR));
                                    ap.getInventory().setItem(slot2, tool3);
                                } else continue;
                                break;
                            }
                            slot2++;
                        }
                    }
                }
            }, 1L);
        }
    }
}
