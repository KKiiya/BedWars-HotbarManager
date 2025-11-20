package me.kiiya.hotbarmanager.api.events;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class HotbarItemSetEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Category category;
    private final String itemPath;
    private final int slot;
    private boolean cancelled;

    @Deprecated
    public HotbarItemSetEvent(Player player, Category category, int slot) {
        this.player = player;
        this.category = category;
        this.itemPath = null;
        this.slot = slot;
        this.cancelled = false;
    }

    public HotbarItemSetEvent(Player player, String itemPath, int slot) {
        this.player = player;
        this.category = null;
        this.itemPath = itemPath;
        this.slot = slot;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public String getItemPath() {
        return itemPath;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
