package me.kiiya.hotbarmanager.api.menu;

import me.kiiya.hotbarmanager.api.hotbar.Category;

import java.util.Set;

/**
 * Manages cached shop items and menus
 */
public interface IShopCacheManager {

    /**
     * Load shop items from config
     * @param shopConfig The shop configuration section
     */
    void loadFromConfig(org.bukkit.configuration.ConfigurationSection shopConfig);

    /**
     * Get the main shop menu
     */
    IShopMenu getMainMenu();

    /**
     * Get page for a specific category
     */
    IPage getPage(Category category);

    /**
     * Get the group name for this cache
     */
    String getGroup();

    /**
     * Check if cache has been loaded
     */
    boolean isLoaded();

    /**
     * Reload the cache from config
     */
    void reload(org.bukkit.configuration.ConfigurationSection shopConfig);

    /**
     * Clear all cached data
     */
    void clear();
}

