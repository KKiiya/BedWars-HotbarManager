package me.kiiya.hotbarmanager.api.events;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHotbarUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IHotbarPlayer player;
    private final Category oldCategory;
    private final Category newCategory;
    private final int slot;
    private boolean cancelled = false;

    public PlayerHotbarUpdateEvent(IHotbarPlayer player, int slot, Category oldCategory, Category newCategory) {
        this.player = player;
        this.oldCategory = oldCategory;
        this.newCategory = newCategory;
        this.slot = slot;
    }

    public IHotbarPlayer getPlayer() {
        return player;
    }

    public Category getOldCategory() {
        return oldCategory;
    }

    public Category getNewCategory() {
        return newCategory;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isCancelled() {
        return cancelled;
    }

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
