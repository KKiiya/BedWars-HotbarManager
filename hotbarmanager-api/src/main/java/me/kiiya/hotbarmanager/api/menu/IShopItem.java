package me.kiiya.hotbarmanager.api.menu;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a cached shop item
 */
public interface IShopItem {

    /**
     * Get the category key (e.g., "blocks-category")
     */
    String getCategoryKey();

    /**
     * Get the category enum
     */
    Category getCategory();

    /**
     * Get the item key (e.g., "wool")
     */
    String getItemKey();

    /**
     * Get the full config path (e.g., "blocks-category.category-content.wool")
     */
    String getFullPath();

    /**
     * Get a clone of the base ItemStack with NBT tag
     */
    ItemStack getBaseItem();

    /**
     * Get the content slot from config
     */
    int getContentSlot();
}