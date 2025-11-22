package me.kiiya.hotbarmanager.menu.helpers;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.menu.*;
import org.bukkit.inventory.ItemStack;

/**
 * Implementation of IShopItem
 */
public class CachedShopItem implements IShopItem {
    private final String categoryKey;
    private final String itemKey;
    private final ItemStack baseItem;
    private final Category category;
    private final int contentSlot;

    public CachedShopItem(String categoryKey, String itemKey, ItemStack baseItem, int contentSlot) {
        this.categoryKey = categoryKey;
        this.category = Category.getFromString(categoryKey.split("-")[0]);
        this.itemKey = itemKey;
        this.baseItem = baseItem;
        this.contentSlot = contentSlot;
    }

    @Override
    public String getCategoryKey() { return categoryKey; }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public String getItemKey() { return itemKey; }

    @Override
    public String getFullPath() { return categoryKey + ".category-content." + itemKey; }

    @Override
    public ItemStack getBaseItem() { return baseItem.clone(); }

    @Override
    public int getContentSlot() { return contentSlot; }
}