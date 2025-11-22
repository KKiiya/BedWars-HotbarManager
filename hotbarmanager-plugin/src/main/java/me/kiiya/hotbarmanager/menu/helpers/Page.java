package me.kiiya.hotbarmanager.menu.helpers;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.menu.IShopItem;
import me.kiiya.hotbarmanager.api.menu.IShopMenu;
import me.kiiya.hotbarmanager.api.menu.IPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page implements IPage {
    private final Menu parent;
    private final int pageNumber;
    private final List<IShopItem> items;
    private final Category category;
    private Page previousPage;
    private Page nextPage;

    public Page(Menu parent, Category category, int pageNumber) {
        this.parent = parent;
        this.category = category;
        this.pageNumber = pageNumber;
        this.items = new ArrayList<>();
    }

    @Override
    public IShopMenu getParent() { return parent; }

    @Override
    public int getPageNumber() { return pageNumber; }

    @Override
    public List<IShopItem> getItems() { return Collections.unmodifiableList(items); }

    @Override
    public Category getCategory() {
        return category;
    }

    public void addItem(IShopItem item) {
        this.items.add(item);
    }

    @Override
    public IPage getPreviousPage() { return previousPage; }

    public void setPreviousPage(Page previousPage) { this.previousPage = previousPage; }

    @Override
    public IPage getNextPage() { return nextPage; }

    public void setNextPage(Page nextPage) { this.nextPage = nextPage; }

    @Override
    public boolean hasPreviousPage() { return previousPage != null; }

    @Override
    public boolean hasNextPage() { return nextPage != null; }
}
