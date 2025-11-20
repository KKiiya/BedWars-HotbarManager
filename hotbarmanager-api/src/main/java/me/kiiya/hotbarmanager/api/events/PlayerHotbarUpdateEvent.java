package me.kiiya.hotbarmanager.api.events;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class PlayerHotbarUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IHotbarPlayer player;
    private final String hotbarOld;
    private final String hotbarNew;
    private final SortType sortType;
    private final int slot;
    private boolean cancelled = false;

    public PlayerHotbarUpdateEvent(IHotbarPlayer player, int slot, Category oldCategory, Category newCategory) {
        this.player = player;
        this.hotbarOld = oldCategory.toString();
        this.hotbarNew = newCategory.toString();
        this.slot = slot;
        this.sortType = null;
    }

    public PlayerHotbarUpdateEvent(IHotbarPlayer player, int slot, String hotbarOld, String hotbarNew, SortType sortType) {
        this.player = player;
        this.hotbarOld = hotbarOld;
        this.hotbarNew = hotbarNew;
        this.slot = slot;
        this.sortType = sortType;
    }

    public IHotbarPlayer getPlayer() {
        return player;
    }

    @Nullable
    public Category getOldCategory() {
        return Category.getFromString(hotbarOld);
    }

    @Nullable
    public Category getNewCategory() {
        return Category.getFromString(hotbarNew);
    }

    public String getHotbarOld() {
        return hotbarOld;
    }

    public String getHotbarNew() {
        return hotbarNew;
    }

    @Nullable
    public SortType getSortType() {
        return sortType;
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
