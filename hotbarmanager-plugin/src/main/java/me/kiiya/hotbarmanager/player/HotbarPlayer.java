package me.kiiya.hotbarmanager.player;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.events.PlayerHotbarResetEvent;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class HotbarPlayer implements IHotbarPlayer {
    private final Player player;
    private final Database db;

    public HotbarPlayer(Player player) {
        db = HotbarManager.getDB();
        this.player = player;
    }

    public HotbarPlayer(UUID uuid) {
        db = HotbarManager.getDB();
        this.player = Bukkit.getServer().getPlayer(uuid);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setSlotCategory(int slot, Category category) {
        String cat = category.toString();
        String slotResult = "slot"+slot;
        db.setData(player, slotResult, cat);
    }

    @Override
    public Category getSlotCategory(int slot) {
        String resultSlot = "slot"+slot;
        return Category.getFromString(db.getData(player, resultSlot));
    }

    @Override
    public List<Category> getHotbarAsList() {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            nums.add(i);
        }

        List<Category> hotbar = new ArrayList<>();
        for (Integer num : nums) {
            String slot = "slot"+num;
            hotbar.add(Category.getFromString(db.getData(player, slot)));
        }
        return Collections.unmodifiableList(hotbar);
    }

    @Override
    public void resetHotbar() {
        PlayerHotbarResetEvent e = new PlayerHotbarResetEvent(this);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) return;

        db.setData(player, "slot0", "MELEE");
        db.setData(player, "slot1", "NONE");
        db.setData(player, "slot2", "NONE");
        db.setData(player, "slot3", "NONE");
        db.setData(player, "slot4", "NONE");
        db.setData(player, "slot5", "NONE");
        db.setData(player, "slot6", "NONE");
        db.setData(player, "slot7", "NONE");

        if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            if (HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                db.setData(player, "slot8", "COMPASS");
            } else {
                db.setData(player, "slot8", "NONE");
            }
        } else {
            if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                db.setData(player, "slot8", "COMPASS");
            } else {
                db.setData(player, "slot8", "NONE");
            }
        }
    }
}
