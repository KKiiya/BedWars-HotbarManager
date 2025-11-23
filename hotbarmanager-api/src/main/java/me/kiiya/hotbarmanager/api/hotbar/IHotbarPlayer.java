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
    @Deprecated
    void setSlotCategory(int slot, Category category);

    /**
     * Set a category for a specified slot
     *
     * @param category - the category you want the player to have
     * @param slot - the slot you want the category to be in
     * @param callEvent - whether to call the event
     */
    void setSlotCategory(int slot, Category category, boolean callEvent);

    /**
     * Set a category for a specified slot
     *
     * @param itemPath - the category you want the player to have
     * @param slot - the slot you want the category to be in
     */
    @Deprecated
    void setSlotCategory(int slot, String itemPath);

    /**
     * Set a category for a specified slot
     *
     * @param itemPath - the category you want the player to have
     * @param slot - the slot you want the category to be in
     * @param callEvent - whether to call the event
     */
    void setSlotCategory(int slot, String itemPath, boolean callEvent);

    /**
     * Get the category in a specific slot
     *
     * @param slot - The slot you want to get (0-8)
     * @return - the category of the slot you are searching for
     */
    Category getSlotCategory(int slot);

    /**
     * Get the category in a specific slot
     *
     * @param slot - The slot you want to get (0-8)
     * @return - the category of the slot you are searching for
     */
    String getItemPath(int slot);

    /**
     * Get the hotbar as a list
     * @return - a list with the categories ordered in a hotbar
     */
    List<Category> getHotbarAsList();

    /**
     * Get the hotbar as a string list. This is recommended to be used for per ITEM saving
     * @return - a list with the categories ordered in a hotbar as strings
     */
    List<String> getHotgarAsStringList();

    /**
     * Reset the hotbar of the player
     */
    void resetHotbar();

    /**
     * Save the hotbar of the player (this will save the hotbar in the database)
     */
    @Deprecated
    void saveHotbar();

    /**
     * Save the hotbar of the player (this will save the hotbar in the database)
     * @param destroy - whether to destroy the hotbar after saving it
     * @param runTask - whether to run the task in a separate thread
     */
    void saveHotbar(boolean destroy, boolean runTask);

    /**
     * Destroy the hotbar player
     */
    @Deprecated
    void destroy();

    /**
     * Destroy the hotbar player
     *
     * @param save - whether to save the hotbar before destroying it
     */
    void destroy(boolean save);

    /**
     * Destroy the hotbar player
     *
     * @param save - whether to save the hotbar before destroying it
     * @param isServerShutdown - whether the server is shutting down
     */
    void destroy(boolean save, boolean isServerShutdown);
}
