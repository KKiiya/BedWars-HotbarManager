package me.kiiya.hotbarmanager.api.hotbar;

import org.bukkit.entity.Player;
import java.util.List;

public interface IHotbarPlayer {

    /**
     * Get the hotbar player as a spigot player
     *
     * @return - the player
     */
    Player getPlayer();

    /**
     * Set a category for a specified slot
     *
     * @param category - the category you want the player to have
     * @param slot - the slot you want the category to be in
     */
    void setSlotCategory(int slot, Category category);

    /**
     * Get the category in a specific slot
     *
     * @param slot - The slot you want to get (0-8)
     * @return - the category of the slot you are searching for
     */
    Category getSlotCategory(int slot);

    /**
     * Get the hotbar as a list
     * @return - a list with the categories ordered in a hotbar
     */
    List<Category> getHotbarAsList();

    /**
     * Reset the hotbar of the player
     */
    void resetHotbar();
}
