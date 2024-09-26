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
    private static final LinkedHashMap<String, HotbarPlayer> hotbarPlayers = new LinkedHashMap<>();
    private HashMap<Integer, Category> hotbar;
    private Player player;
    private Database db;

    public HotbarPlayer(Player player) {
        this.db = HotbarManager.getPlugins().getDB();
        this.player = player;
        this.hotbar = new HashMap<>();
        Bukkit.getScheduler().runTask(HotbarManager.getPlugins(), () -> {
            for (int i = 0; i < 9; i++) {
                hotbar.put(i, Category.getFromString(this.db.getData(player, "slot" + i)));
            }
        });
        hotbarPlayers.put(player.getUniqueId().toString(), this);
    }

    public HotbarPlayer(UUID uuid) {
        this.db = HotbarManager.getPlugins().getDB();
        this.player = Bukkit.getServer().getPlayer(uuid);
        this.hotbar = new HashMap<>();
        for (int i = 0; i < 9; i++) hotbar.put(i, Category.NONE);
        Bukkit.getScheduler().runTask(HotbarManager.getPlugins(), () -> {
            for (int i = 0; i < 9; i++) {
                hotbar.put(i, Category.getFromString(this.db.getData(player, "slot" + i)));
            }
        });
        hotbarPlayers.put(player.getUniqueId().toString(), this);
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

        hotbar.put(0, Category.MELEE);
        hotbar.put(1, Category.NONE);
        hotbar.put(2, Category.NONE);
        hotbar.put(3, Category.NONE);
        hotbar.put(4, Category.NONE);
        hotbar.put(5, Category.NONE);
        hotbar.put(6, Category.NONE);
        hotbar.put(7, Category.NONE);

        if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            if (HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                hotbar.put(8, Category.COMPASS);
            } else {
                hotbar.put(8, Category.NONE);
            }
        } else {
            if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                hotbar.put(8, Category.COMPASS);
            } else {
                hotbar.put(8, Category.NONE);
            }
        }
    }

    public void saveHotbar() {
        Bukkit.getScheduler().runTask(HotbarManager.getPlugins(), () -> {
            for (int i = 0; i < 9; i++) {
                this.db.setData(player, "slot" + i, hotbar.get(i).toString());
            }
        });
    }

    @Override
    public void destroy() {
        saveHotbar();
        hotbarPlayers.remove(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
            hotbar = null;
            player = null;
            db = null;
        }, 10L);
    }

    public static HotbarPlayer getHotbarPlayer(Player player) {
        return hotbarPlayers.get(player.getUniqueId().toString());
    }

    public static HotbarPlayer getHotbarPlayer(UUID uuid) {
        return hotbarPlayers.get(uuid.toString());
    }
}
