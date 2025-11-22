package me.kiiya.hotbarmanager.api.menu;

import me.kiiya.hotbarmanager.api.hotbar.Category;

import java.util.List;

/**
 * Represents a shop menu for a category
 */
public interface IShopMenu {

    /**
     * Get all pages in this menu
     */
    List<IPage> getPages();

    /**
     * Get a specific page by number
     * @param pageNumber The page number (0-indexed)
     * @return The page or null if page number is invalid
     */
    IPage getPage(int pageNumber);

    /**
     * Get a page by category
     * @param category The category
     * @return The page or null if not found
     */
    IPage getPage(Category category);

    /**
     * Get the first page
     * @return First page or null if menu is empty
     */
    IPage getFirstPage();

    /**
     * Get an item by its key
     * @param itemKey The item key (e.g., "wool")
     * @return The cached item or null if not found
     */
    IShopItem getItem(String itemKey);

    /**
     * Get total number of pages
     */
    int getTotalPages();
}
