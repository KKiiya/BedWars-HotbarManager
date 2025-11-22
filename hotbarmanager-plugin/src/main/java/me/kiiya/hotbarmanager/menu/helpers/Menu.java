package me.kiiya.hotbarmanager.menu.helpers;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.menu.IShopItem;
import me.kiiya.hotbarmanager.api.menu.IShopMenu;
import me.kiiya.hotbarmanager.api.menu.IPage;

import java.util.*;

public class Menu implements IShopMenu {
    private final List<IPage> pages;
    private final Map<String, IShopItem> itemsByKey;

    public Menu() {
        this.pages = new ArrayList<>();
        this.itemsByKey = new HashMap<>();
    }

    @Override
    public List<IPage> getPages() { return Collections.unmodifiableList(pages); }

    @Override
    public IPage getPage(int pageNumber) {
        return pageNumber >= 0 && pageNumber < pages.size() ? pages.get(pageNumber) : null;
    }

    @Override
    public IPage getPage(Category category) {
        for (IPage page : pages) {
            if (page.getCategory() == category) return page;
        }
        return null;
    }

    @Override
    public IPage getFirstPage() {
        return pages.isEmpty() ? null : pages.get(0);
    }

    @Override
    public IShopItem getItem(String itemKey) {
        return itemsByKey.get(itemKey);
    }

    @Override
    public int getTotalPages() {
        return pages.size();
    }

    public void addPage(Page page) {
        pages.add(page);

        // Link pages for navigation
        if (pages.size() > 1) {
            Page previous = (Page) pages.get(pages.size() - 2);
            previous.setNextPage(page);
            page.setPreviousPage(previous);
        }

        // Register all items from the page
        for (IShopItem item : page.getItems()) {
            itemsByKey.put(item.getItemKey(), item);
        }
    }
}

