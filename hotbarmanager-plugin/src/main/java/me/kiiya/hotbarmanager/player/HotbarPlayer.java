package me.kiiya.hotbarmanager.player;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.events.PlayerHotbarResetEvent;
import me.kiiya.hotbarmanager.api.events.PlayerHotbarUpdateEvent;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.Support;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.kiiya.hotbarmanager.utils.Utility.debug;

public class HotbarPlayer implements IHotbarPlayer {

    private HashMap<Integer, Category> hotbar;
    private Player player;
    private Database db;

    public HotbarPlayer(Player player) {
        debug("Creating HotbarPlayer for " + player.getName());
        this.db = HotbarManager.getInstance().getDB();
        this.player = player;
        this.hotbar = new HashMap<>();
        HashMap<String, String> data;

        try {
            data = db.getData(player);
            for (int i = 0; i < 9; i++) {
                String category = data.get("slot" + i);
                debug("Loading slot " + i + " for " + player.getName() + " with value " + category);
                hotbar.put(i, Category.getFromString(category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        debug("Creating HotbarPlayer for " + player.getName() + " was successful.");
    }

    public HotbarPlayer(UUID uuid) {
        this.db = HotbarManager.getInstance().getDB();
        this.player = Bukkit.getServer().getPlayer(uuid);
        debug("Creating HotbarPlayer for " + player.getName());
        this.hotbar = new HashMap<>();
        HashMap<String, String> data;

        try {
            data = db.getData(player);
            for (int i = 0; i < 9; i++) {
                String category = data.get("slot" + i);
                debug("Loading slot " + i + " for " + player.getName() + " with value " + category);
                hotbar.put(i, Category.getFromString(category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        debug("Creating HotbarPlayer for " + player.getName() + " was successful.");
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setSlotCategory(int slot, Category category) {
        setSlotCategory(slot, category, true);
    }

    @Override
    public void setSlotCategory(int slot, Category newCategory, boolean callEvent) {
        Category oldCategory = hotbar.get(slot);
        if (oldCategory == newCategory) return;

        debug("Setting slot " + slot + " for " + player.getName() + ". OLD: " + oldCategory.toString() + ", NEW: " + newCategory.toString());
        PlayerHotbarUpdateEvent event = new PlayerHotbarUpdateEvent(this, slot, oldCategory, newCategory);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            debug("Setting slot " + slot + " for " + player.getName() + " was cancelled.");
            return;
        }

        hotbar.put(slot, newCategory);
        debug("Setting slot " + slot + " for " + player.getName() + " was successful.");
    }

    @Override
    public Category getSlotCategory(int slot) {
        return hotbar.get(slot);
    }

    @Override
    public List<Category> getHotbarAsList() {
        List<Category> hotbar = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            hotbar.add(getSlotCategory(i));
        }
        return Collections.unmodifiableList(hotbar);
    }

    @Override
    public void resetHotbar() {
        debug("Resetting Hotbar for " + player.getName());
        PlayerHotbarResetEvent e = new PlayerHotbarResetEvent(this);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            debug("Resetting Hotbar for " + player.getName() + " was cancelled.");
            return;
        }

        List<Category> defaultSlots = me.kiiya.hotbarmanager.player.HotbarManager.getInstance().getDefaultSlots();
        for (int i = 0; i < defaultSlots.size(); i++) {

            if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
                if (HotbarManager.getMainConfig().getBoolean("enable-compass-support") && defaultSlots.get(i) == Category.COMPASS) {
                    debug("Resetting slot " + i + " for " + player.getName() + ". OLD: " + hotbar.get(i).toString() + ", NEW: " + Category.COMPASS);
                    hotbar.put(8, Category.COMPASS);
                    continue;
                }
                else {
                    debug("Resetting slot " + i + " for " + player.getName() + ". OLD: " + hotbar.get(i).toString() + ", NEW: " + Category.NONE);
                    hotbar.put(i, Category.NONE);
                }
            } else {
                if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support") && defaultSlots.get(i) == Category.COMPASS) {
                    debug("Resetting slot " + i + " for " + player.getName() + ". OLD: " + hotbar.get(i).toString() + ", NEW: " + Category.COMPASS);
                    hotbar.put(8, Category.COMPASS);
                    continue;
                }
                else {
                    debug("Resetting slot " + i + " for " + player.getName() + ". OLD: " + hotbar.get(i).toString() + ", NEW: " + defaultSlots.get(i).toString());
                    hotbar.put(i, Category.NONE);
                }
            }
            debug("Resetting slot " + i + " for " + player.getName() + ". OLD: " + hotbar.get(i).toString() + ", NEW: " + defaultSlots.get(i).toString());
            hotbar.put(i, defaultSlots.get(i));
        }
        debug("Resetting Hotbar for " + player.getName() + " was successful.");
    }

    @Deprecated
    public void saveHotbar() {
        debug("Saving Hotbar for " + player.getName());
        Bukkit.getScheduler().runTask(HotbarManager.getInstance(), this::save);
        debug("Saving Hotbar for " + player.getName() + " was successful.");
    }

    @Override
    public void saveHotbar(boolean destroy, boolean runTask) {
        debug("Saving Hotbar for " + player.getName());
        if (runTask) {
            Bukkit.getScheduler().runTask(HotbarManager.getInstance(), () -> {
                save();
                if (destroy) destroy(false);
            });
        } else {
            save();
            if (destroy) destroy(false);
        }
        debug("Saving Hotbar for " + player.getName() + " was successful.");
    }

    private void save() {
        if (hotbar == null) {
            debug("Hotbar is null, skipping save.");
            return;
        }
        for (int i = 0; i < 9; i++) {
            Category category = hotbar.get(i);
            if (category != null) {
                debug("Saving slot " + i + " for " + player.getName() + " with value " + category.toString());
                db.setData(player, "slot" + i, category.toString());
            } else {
                debug("Slot " + i + " for " + player.getName() + " is null, skipping save.");
            }
        }
    }

    @Override
    @Deprecated
    public void destroy() {
        debug("Destroying HotbarPlayer for " + player.getName());
        saveHotbar();
        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
            hotbar = null;
            player = null;
            db = null;
        }, 10L);
        me.kiiya.hotbarmanager.player.HotbarManager.getPrivateInstance().getPlayersMap().remove(player.getUniqueId().toString());
    }

    @Override
    public void destroy(boolean save) {
        debug("Destroying HotbarPlayer for " + player.getName());
        if (save) saveHotbar();
        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
            hotbar = null;
            player = null;
            db = null;
        }, 10L);
        me.kiiya.hotbarmanager.player.HotbarManager.getPrivateInstance().getPlayersMap().remove(player.getUniqueId().toString());
    }
}
