package me.kiiya.hotbarmanager.menu.helpers;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.menu.IPage;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.api.menu.IShopMenu;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CacheManager implements IShopCacheManager {

    private static final HashMap<String, IShopCacheManager> instances = new HashMap<>();
    private IShopMenu mainMenu;
    private final VersionSupport versionSupport;
    private final String group;

    public CacheManager(String group, VersionSupport versionSupport) {
        this.group = group;
        this.versionSupport = versionSupport;
        instances.put(group, this);
    }

    @Override
    public void loadFromConfig(ConfigurationSection shopConfig) {
        Menu menu = new Menu();

        // Iterate through all categories
        for (String categoryKey : shopConfig.getKeys(false)) {
            if (!categoryKey.endsWith("-category")) continue;
            Category category = Category.getFromString(categoryKey.split("-")[0]);
            if (category == null) continue;

            ConfigurationSection categorySection = shopConfig.getConfigurationSection(categoryKey);
            if (categorySection == null) continue;

            ConfigurationSection contentSection = categorySection.getConfigurationSection("category-content");
            if (contentSection == null) continue;

            List<CachedShopItem> allItems = new ArrayList<>();

            // Load all items from category
            for (String itemKey : contentSection.getKeys(false)) {
                CachedShopItem cachedItem = loadItem(category, itemKey, contentSection);
                if (cachedItem != null) {
                    allItems.add(cachedItem);
                }
            }

            // Sort items by content-slot
            allItems.sort(Comparator.comparingInt(CachedShopItem::getContentSlot));

            // Create ONE page for this category (not multiple pages)
            if (!allItems.isEmpty()) {
                Page page = new Page(menu, category, menu.getPages().size());
                for (CachedShopItem item : allItems) {
                    page.addItem(item);
                }
                menu.addPage(page);
            }
        }

        this.mainMenu = menu;
    }

    @Override
    public IShopMenu getMainMenu() {
        return mainMenu;
    }

    private CachedShopItem loadItem(Category category, String itemKey, ConfigurationSection contentSection) {
        String categoryKey = category.toString().toLowerCase() + "-category";
        ConfigurationSection itemSection = contentSection.getConfigurationSection(itemKey);
        if (itemSection == null) return null;

        ConfigurationSection settings = itemSection.getConfigurationSection("content-settings");
        if (settings == null) return null;

        int contentSlot = settings.getInt("content-slot", -1);
        if (contentSlot == -1) return null;

        // Get tier1 item for base material
        ConfigurationSection tiers = itemSection.getConfigurationSection("content-tiers");
        if (tiers == null) return null;

        ConfigurationSection tier1 = tiers.getConfigurationSection("tier1");
        if (tier1 == null) return null;

        ConfigurationSection buyItems = tier1.getConfigurationSection("buy-items");
        if (buyItems == null) return null;

        // Check if all items auto-equip (armor) - if so, skip this item
        boolean allAutoEquip = true;
        for (String buyItemKey : buyItems.getKeys(false)) {
            ConfigurationSection buyItemSection = buyItems.getConfigurationSection(buyItemKey);
            if (buyItemSection != null && !buyItemSection.getBoolean("auto-equip", false)) {
                allAutoEquip = false;
                break;
            }
        }

        // Skip items where all buy-items auto-equip (like armor)
        if (allAutoEquip) return null;

        // Get first non-auto-equip item for display
        ItemStack baseItem = null;
        for (String buyItemKey : buyItems.getKeys(false)) {
            ConfigurationSection buyItemSection = buyItems.getConfigurationSection(buyItemKey);
            if (buyItemSection == null) continue;

            if (!buyItemSection.getBoolean("auto-equip", false)) {
                String material = buyItemSection.getString("material");
                int data = buyItemSection.getInt("data", 0);

                if (material != null) {
                    try {
                        baseItem = new ItemStack(Material.valueOf(material), 1, (short) data);
                        break;
                    } catch (IllegalArgumentException e) {
                        // Invalid material, skip
                    }
                }
            }
        }

        if (baseItem == null) return null;

        // Add NBT tag with itemKey
        baseItem = versionSupport.setItemTag(baseItem, "hbm", category.toString().toLowerCase() + "." + itemKey);

        // Create cached item
        return new CachedShopItem(categoryKey, itemKey, baseItem, contentSlot);
    }

    @Override
    public IPage getPage(Category category) {
        return mainMenu.getPage(category);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean isLoaded() {
        return mainMenu != null && !mainMenu.getPages().isEmpty();
    }

    @Override
    public void reload(ConfigurationSection shopConfig) {
        loadFromConfig(shopConfig);
    }

    @Override
    public void clear() {
        mainMenu = null;
    }

    public static IShopCacheManager getInstance(String group) {
        return instances.get(group);
    }
}
