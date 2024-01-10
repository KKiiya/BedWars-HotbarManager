package me.kiiya.hotbarmanager.api;

import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface HotbarManager {

    /**
     * Get the HotbarPlayer
     * @param player - the player
     * @return - the HotbarPlayer
     */
    IHotbarPlayer getHotbarPlayer(Player player);

    /**
     * Get the HotbarPlayer
     * @param uuid - the uuid of the player
     * @return - the HotbarPlayer
     */
    IHotbarPlayer getHotbarPlayer(UUID uuid);

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
    }
}
