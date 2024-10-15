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

public class HotbarPlayer implements IHotbarPlayer {

    private HashMap<Integer, Category> hotbar;
    private Player player;
    private Database db;

    public HotbarPlayer(Player player) {
        this.db = HotbarManager.getInstance().getDB();
        this.player = player;
        this.hotbar = new HashMap<>();
        Bukkit.getScheduler().runTask(HotbarManager.getInstance(), () -> {
            for (int i = 0; i < 9; i++) {
                hotbar.put(i, Category.getFromString(this.db.getData(player, "slot" + i)));
            }
        });
    }

    public HotbarPlayer(UUID uuid) {
        this.db = HotbarManager.getInstance().getDB();
        this.player = Bukkit.getServer().getPlayer(uuid);
        this.hotbar = new HashMap<>();
        for (int i = 0; i < 9; i++) hotbar.put(i, Category.NONE);
        Bukkit.getScheduler().runTask(HotbarManager.getInstance(), () -> {
            for (int i = 0; i < 9; i++) {
                hotbar.put(i, Category.getFromString(this.db.getData(player, "slot" + i)));
            }
        });
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
        PlayerHotbarUpdateEvent event = new PlayerHotbarUpdateEvent(this, slot, oldCategory, newCategory);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        hotbar.put(slot, newCategory);
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
        PlayerHotbarResetEvent e = new PlayerHotbarResetEvent(this);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) return;

        List<Category> defaultSlots = me.kiiya.hotbarmanager.player.HotbarManager.getInstance().getDefaultSlots();
        for (int i = 0; i < defaultSlots.size(); i++) {

            if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
                if (HotbarManager.getMainConfig().getBoolean("enable-compass-support") && defaultSlots.get(i) == Category.COMPASS) {
                    hotbar.put(8, Category.COMPASS);
                    continue;
                }
                else hotbar.put(i, Category.NONE);
            } else {
                if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support") && defaultSlots.get(i) == Category.COMPASS) {
                    hotbar.put(8, Category.COMPASS);
                    continue;
                }
                else hotbar.put(i, Category.NONE);
            }
            hotbar.put(i, defaultSlots.get(i));
        }
    }

    public void saveHotbar() {
        Bukkit.getScheduler().runTask(HotbarManager.getInstance(), () -> {
            for (int i = 0; i < 9; i++) {
                this.db.setData(player, "slot" + i, hotbar.get(i).toString());
            }
        });
    }

    @Override
    public void destroy() {
        saveHotbar();
        me.kiiya.hotbarmanager.player.HotbarManager.getPrivateInstance().getPlayersMap().remove(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
            hotbar = null;
            player = null;
            db = null;
        }, 10L);
    }
}
