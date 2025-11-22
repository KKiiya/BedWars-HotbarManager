package me.kiiya.hotbarmanager.api.menu;

import me.kiiya.hotbarmanager.api.hotbar.Category;

import java.util.List;

/**
 * Represents a page in a shop menu
 */
public interface IPage {

    /**
     * Get the parent menu
     */
    IShopMenu getParent();

    /**
     * Get the page number (0-indexed)
     */
    int getPageNumber();

    /**
     * Get all items in this page
     */
    List<IShopItem> getItems();

    /**
     * Get the category of this page
     */
    Category getCategory();

    /**
     * Get the previous page
     * @return Previous page or null if this is the first page
     */
    IPage getPreviousPage();

    /**
     * Get the next page
     * @return Next page or null if this is the last page
     */
    IPage getNextPage();

    /**
     * Check if there is a previous page
     */
    boolean hasPreviousPage();

    /**
     * Check if there is a next page
     */
    boolean hasNextPage();
}