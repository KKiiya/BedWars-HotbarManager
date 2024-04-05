package me.kiiya.hotbarmanager.player;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.events.PlayerHotbarResetEvent;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.Support;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class HotbarPlayer implements IHotbarPlayer {
    private static final Map<UUID, HotbarPlayer> hotbarPlayers = new HashMap<>();
    private Player player;
    private Database db;
    private HashMap<Integer, Category> hotbar = new HashMap<>();

    public HotbarPlayer(Player player) {
        db = HotbarManager.getDB();
        this.player = player;
        Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getPlugins(), () -> {
            hotbar.put(0, Category.getFromString(db.getData(player, "slot0")));
            hotbar.put(1, Category.getFromString(db.getData(player, "slot1")));
            hotbar.put(2, Category.getFromString(db.getData(player, "slot2")));
            hotbar.put(3, Category.getFromString(db.getData(player, "slot3")));
            hotbar.put(4, Category.getFromString(db.getData(player, "slot4")));
            hotbar.put(5, Category.getFromString(db.getData(player, "slot5")));
            hotbar.put(6, Category.getFromString(db.getData(player, "slot6")));
            hotbar.put(7, Category.getFromString(db.getData(player, "slot7")));
            hotbar.put(8, Category.getFromString(db.getData(player, "slot8")));
        });
        hotbarPlayers.put(player.getUniqueId(), this);
    }

    public HotbarPlayer(UUID uuid) {
        db = HotbarManager.getDB();
        this.player = Bukkit.getServer().getPlayer(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getPlugins(), () -> {
            hotbar.put(0, Category.getFromString(db.getData(player, "slot0")));
            hotbar.put(1, Category.getFromString(db.getData(player, "slot1")));
            hotbar.put(2, Category.getFromString(db.getData(player, "slot2")));
            hotbar.put(3, Category.getFromString(db.getData(player, "slot3")));
            hotbar.put(4, Category.getFromString(db.getData(player, "slot4")));
            hotbar.put(5, Category.getFromString(db.getData(player, "slot5")));
            hotbar.put(6, Category.getFromString(db.getData(player, "slot6")));
            hotbar.put(7, Category.getFromString(db.getData(player, "slot7")));
            hotbar.put(8, Category.getFromString(db.getData(player, "slot8")));
        });
        hotbarPlayers.put(uuid, this);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setSlotCategory(int slot, Category category) {
        hotbar.put(slot, category);
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
        for (int i = 0; i < 9; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getPlugins(), () -> {
                db.setData(player, "slot" + finalI, hotbar.get(finalI).toString());
            });
        }
    }

    @Override
    public void destroy() {
        hotbarPlayers.remove(player.getUniqueId());
        hotbar = null;
        player = null;
        db = null;
    }

    public static HotbarPlayer getHotbarPlayer(Player player) {
        return hotbarPlayers.get(player.getUniqueId());
    }

    public static HotbarPlayer getHotbarPlayer(UUID uuid) {
        return hotbarPlayers.get(uuid);
    }
}
