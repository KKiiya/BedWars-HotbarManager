package me.kiiya.hotbarmanager.api.events;

import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHotbarResetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IHotbarPlayer player;

    private boolean cancelled = false;

    public PlayerHotbarResetEvent(IHotbarPlayer player) {
        this.player = player;
    }

    public IHotbarPlayer getPlayer() {
        return player;
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
