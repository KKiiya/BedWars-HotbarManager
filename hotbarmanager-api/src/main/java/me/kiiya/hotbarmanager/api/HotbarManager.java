package me.kiiya.hotbarmanager.api;

import me.kiiya.hotbarmanager.api.hotbar.IHotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface HotbarManager {

    /**
     * Get the SortType for hotbars (per category or per item)
     */
    SortType getSortType();

    /**
     * Get the HotbarPlayer
     * @param player - the player
     * @return - the HotbarPlayer
     */
    @Deprecated
    IHotbarPlayer getHotbarPlayer(Player player);

    /**
     * Get the HotbarPlayer
     * @param uuid - the uuid of the player
     * @return - the HotbarPlayer
     */
    @Deprecated
    IHotbarPlayer getHotbarPlayer(UUID uuid);

    /**
     * Get the HotbarManager
     * @return - the HotbarManager
     */
    IHotbarManager getHotbarManager();

    /**
     * Get the CacheManager
     *
     * @return - the CacheManager
     */
    IShopCacheManager getCacheManager(String group);

    /**
     * Get the MenuUtil
     */
    MenuUtil getMenuUtil();


    interface MenuUtil {
        /**
         * Open the hotbar manager menu
         * @param player - the player
         */
        void openHotbarMenu(Player player);

        /**
         * Open the per item hotbar manager menu for the default category
         * @param player - the player
         */
        void openPerItemMenu(Player player);

        /**
         * Open the per item hotbar manager menu for a specific group
         * @param player - the player
         * @param group - the group name
         */
        void openPerItemMenu(Player player, String group);
    }
}
