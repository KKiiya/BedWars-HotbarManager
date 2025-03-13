package me.kiiya.hotbarmanager.api.hotbar;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IHotbarManager {

    /**
     * Retrieves the hotbar player data for a specific OfflinePlayer.
     * This method returns the corresponding IHotbarPlayer instance which manages the player's hotbar setup,
     * even if the player is not currently online.
     *
     * @param player The OfflinePlayer whose hotbar data is to be retrieved.
     * @return An instance of IHotbarPlayer that holds the hotbar data for the given player.
     */
    IHotbarPlayer getHotbarPlayer(OfflinePlayer player);

    /**
     * Retrieves the hotbar player data for a player based on their UUID.
     * This method is useful for fetching hotbar information even when only the player's UUID is available.
     *
     * @param uuid The UUID of the player whose hotbar data is to be retrieved.
     * @return An instance of IHotbarPlayer associated with the given UUID.
     */
    IHotbarPlayer getHotbarPlayer(UUID uuid);

    /**
     * Returns the default hotbar slots configuration.
     * The default slots are associated with specific categories that define what kind of items or functionalities
     * will be placed in the player's hotbar. This method provides access to the default hotbar layout.
     *
     * @return A Set of Category objects representing the default slots in the hotbar.
     */
    List<Category> getDefaultSlots();


    /**
     * Saves the hotbar data for all players to the database.
     * @param destroy - If true, the hotbar data will be removed from memory after saving.
     */
    void saveHotbars(boolean destroy);
}
