package me.kiiya.hotbarmanager.menu;

import com.andrei1058.bedwars.shop.ShopManager;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.menu.*;
import me.kiiya.hotbarmanager.api.menu.GUIHolder;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class ShopInventoryManager implements GUIHolder {
    private final Player player;
    private final IHotbarPlayer hp;
    private final IShopMenu shopMenu;
    private final IShopCacheManager cacheManager;
    private final String group;
    private IPage currentPage;
    private Inventory inventory;
    private final VersionSupport vs;

    public ShopInventoryManager(Player player, String group) {
        this.player = player;
        this.group = group;
        this.vs = HotbarManager.getVersionSupport();
        this.hp = HotbarManager.getManager().getHotbarPlayer(player);

        // Get cached menu from API
        this.cacheManager = HotbarManager.getCacheManager(group) == null ? HotbarManager.getCacheManager("default") : HotbarManager.getCacheManager(group);
        this.shopMenu = cacheManager.getMainMenu();

        // Start at first page
        this.currentPage = shopMenu.getFirstPage();

        createInventory();
        populateInventory(true);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void createInventory() {
        String title = Utility.getMsg(player, "shop-items-messages." + currentPage.getCategory().toString().toLowerCase() + "-category" + ".inventory-name");
        inventory = Bukkit.createInventory(this, 54, title);
    }

    private void populateInventory(boolean isFirstTime) {
        // Clear inventory
        inventory.clear();
        if (!isFirstTime) createInventory();

        // Add items from current
        MainConfig mc = HotbarManager.getMainConfig();
        ItemStack separator = new ItemStack(Material.valueOf(mc.getString(SEPARATOR_MATERIAL)), 1, (byte) mc.getInt(SEPARATOR_DATA));
        ItemMeta separator_meta = separator.getItemMeta();
        separator_meta.setDisplayName((Utility.getMsg(player, SEPARATOR_NAME)));
        separator_meta.setLore(Utility.getListMsg(player, SEPARATOR_LORE));
        separator_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        separator.setItemMeta(separator_meta);
        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, vs.setItemTag(separator, "hbm", "separator"));
        }

        for (IShopItem cachedItem : currentPage.getItems()) {
            ItemStack displayItem = cachedItem.getBaseItem();
            ItemMeta meta = displayItem.getItemMeta();

            // Apply language-specific name and lore
            String namePath = "shop-items-messages." + cachedItem.getCategoryKey() + ".content-item-" + cachedItem.getItemKey() + "-name";

            meta.setDisplayName(cleanTierPlaceholders(
                    Utility.getMsg(player, namePath)
                    .replace("%bw_color%", Utility.getMsg(player, INVENTORY_COLOR_CODE))
                    .replace("{color}", Utility.getMsg(player, INVENTORY_COLOR_CODE))
                    )
            );
            meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE)
                    .stream().map(s -> s
                            .replace("{category}", cleanPlaceholders(Utility.getMsg(player, namePath))
                            )
                    ).collect(Collectors.toList())
            );
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
            displayItem.setItemMeta(meta);

            // Place in inventory at slot
            inventory.setItem(cachedItem.getContentSlot(), displayItem);
        }

        // Add navigation buttons if needed
        if (currentPage.hasPreviousPage()) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta meta = prevButton.getItemMeta();
            meta.setDisplayName(Utility.getMsg(player, INVENTORY_PREVIOUS_PAGE_ITEM_NAME)
                    .replace("{page}", String.valueOf(currentPage.getPreviousPage().getPageNumber()+1)));
            meta.setLore(Utility.getListMsg(player, INVENTORY_PREVIOUS_PAGE_ITEM_LORE));
            prevButton.setItemMeta(meta);
            inventory.setItem(48, vs.setItemTag(prevButton, "hbm", "prev-page"));
        }

        if (currentPage.hasNextPage()) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta meta = nextButton.getItemMeta();
            meta.setDisplayName(Utility.getMsg(player, INVENTORY_NEXT_PAGE_ITEM_NAME)
                    .replace("{page}", String.valueOf(currentPage.getNextPage().getPageNumber()+1)));
            meta.setLore(Utility.getListMsg(player, INVENTORY_NEXT_PAGE_ITEM_LORE));
            nextButton.setItemMeta(meta);
            inventory.setItem(50, vs.setItemTag(nextButton, "hbm", "next-page"));
        }

        // Add page indicator
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta meta = pageInfo.getItemMeta();
        meta.setDisplayName(Utility.getMsg(player, INVENTORY_CURRENT_PAGE_ITEM_NAME)
                .replace("{page}", String.valueOf(currentPage.getPageNumber()+1))
                .replace("{total-pages}", String.valueOf(shopMenu.getTotalPages()+1)));
        meta.setLore(Utility.getListMsg(player, INVENTORY_CURRENT_PAGE_ITEM_LORE).stream()
                .map(line -> line
                        .replace("{page}", String.valueOf(currentPage.getPageNumber()+1))
                        .replace("{total-pages}", String.valueOf(shopMenu.getTotalPages()+1)))
                .collect(Collectors.toList()));
        pageInfo.setItemMeta(meta);
        inventory.setItem(49, pageInfo);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta back_meta = back.getItemMeta();
        if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_LOBBY_NAME)));
            back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_LOBBY_LORE));
        } else {
            boolean isPlaying = false;
            if (HotbarManager.getSupport() == Support.BEDWARS1058) isPlaying = HotbarManager.getBW1058Api().getArenaUtil().isPlaying(player);
            else if (HotbarManager.getSupport() == Support.BEDWARS2023) isPlaying = HotbarManager.getBW2023Api().getArenaUtil().isPlaying(player);

            if (isPlaying) {
                back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_QUICK_BUY_NAME)));
                back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_QUICK_BUY_LORE));
            } else {
                back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_LOBBY_NAME)));
                back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_LOBBY_LORE));
            }
        }
        back.setItemMeta(back_meta);
        inventory.setItem(45, vs.setItemTag(back, "hbm", "back"));

        ItemStack reset = new ItemStack(Material.valueOf(mc.getString(HOTBAR_RESET_MATERIAL)), 1, (byte) mc.getInt(HOTBAR_RESET_DATA));
        ItemMeta reset_meta = reset.getItemMeta();
        reset_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_RESET_NAME)));
        reset_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_RESET_LORE));
        reset.setItemMeta(reset_meta);
        inventory.setItem(53, vs.setItemTag(reset, "hbm", "reset"));

        // Add hotbar contents
        addHotbarContents();
        if (!isFirstTime) {
            player.openInventory(inventory);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        int slot = e.getSlot();

        if (cursor != null && cursor.getType() != Material.AIR) {
            String cursorTag = vs.getItemTag(cursor, "hbm");
            if (cursorTag == null) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
                return;
            }

            if (slot >= 0 && slot < 9) {
                hp.setSlotCategory(slot, cursorTag, true);
                player.setItemOnCursor(new ItemStack(Material.AIR));
                addHotbarContents();
            }
            return;
        }

        if (clicked == null || clicked.getType() == Material.AIR) return;
        String tag = vs.getItemTag(clicked, "hbm");
        if (tag == null) return;

        switch (tag) {
            case "separator":
            case "placeholder-none":
                // Do nothing
                break;
            case "prev-page":
                if (currentPage.hasPreviousPage()) {
                    currentPage = currentPage.getPreviousPage();
                    populateInventory(false);
                }
                break;
            case "next-page":
                if (currentPage.hasNextPage()) {
                    currentPage = currentPage.getNextPage();
                    populateInventory(false);
                }
                break;
            case "back":
                boolean isProxy = HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023;
                if (isProxy) {
                    if (HotbarManager.getMainConfig().getString(BACK_COMMAND).equalsIgnoreCase("close")) player.closeInventory();
                    else player.performCommand(HotbarManager.getMainConfig().getString(BACK_COMMAND));
                } else {
                    boolean isPlaying = false;
                    if (HotbarManager.getSupport() == Support.BEDWARS1058) isPlaying = HotbarManager.getBW1058Api().getArenaUtil().isPlaying(player);
                    else if (HotbarManager.getSupport() == Support.BEDWARS2023) isPlaying = HotbarManager.bw2023Api.getArenaUtil().isPlaying(player);

                    if (isPlaying) {
                        if (HotbarManager.getSupport() == Support.BEDWARS1058) ShopManager.shop.open(player, PlayerQuickBuyCache.getQuickBuyCache(player.getUniqueId()), false);
                        else com.tomkeuper.bedwars.shop.ShopManager.shop.open(player, com.tomkeuper.bedwars.shop.quickbuy.PlayerQuickBuyCache.getInstance().getQuickBuyCache(player.getUniqueId()), false);
                    } else {
                        if (HotbarManager.getMainConfig().getString(BACK_COMMAND).equalsIgnoreCase("close")) player.closeInventory();
                        else player.performCommand(HotbarManager.getMainConfig().getString(BACK_COMMAND));
                    }
                }
                break;
            case "reset":
                hp.resetHotbar();
                addHotbarContents();
                break;
            default:
                if (slot >= 0 && slot <= 9) {
                    hp.setSlotCategory(slot, "none", true);
                    addHotbarContents();
                } else player.setItemOnCursor(vs.setItemTag(clicked.clone(), "hbm", tag));
                break;
        }
    }

    @Override
    public void onInventoryDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    private void addHotbarContents() {
        boolean usePlaceholderNone = HotbarManager.getMainConfig().getBoolean(PLACEHOLDER_NONE_ENABLED);
        ItemStack placeholder = new ItemStack(Material.AIR);
        if (usePlaceholderNone) {
            placeholder = new ItemStack(Material.valueOf(HotbarManager.getMainConfig().getString(PLACEHOLDER_NONE_MATERIAL)));
            ItemMeta meta = placeholder.getItemMeta();
            meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_PLACEHOLDER_LORE));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
            placeholder.setItemMeta(meta);
            placeholder.setDurability((short) HotbarManager.getMainConfig().getInt(PLACEHOLDER_NONE_DATA));
        }

        for (int slot = 0; slot < 9; slot++) {
            String itemPath = hp.getItemPath(slot); // Format: "category.item" (e.g., "blocks.wool")

            if (itemPath == null || itemPath.isEmpty() || itemPath.equalsIgnoreCase("none") || Category.getFromString(itemPath) != Category.NONE) {
                // Handle empty/none slots
                if (usePlaceholderNone) {
                    ItemMeta meta = placeholder.getItemMeta();
                    meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_PLACEHOLDER_NAME)
                            .replace("{slot}", String.valueOf(slot + 1)));
                    placeholder.setItemMeta(meta);
                    inventory.setItem(slot, vs.setItemTag(placeholder.clone(), "hbm", "placeholder-none"));
                }
                else inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }

            // Parse the path: "category.item" -> ["category", "item"]
            String[] parts = itemPath.split("\\.", 2);
            if (parts.length != 2) {
                // Invalid format, skip
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }

            String categoryName = parts[0]; // e.g., "blocks"
            String itemKey = parts[1];      // e.g., "wool"

            // Convert category name to Category enum
            Category category = Category.getFromString(categoryName);
            if (category == null || category == Category.NONE) {
                // Invalid category, skip
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }

            IPage menu = cacheManager.getPage(category);

            if (menu == null) {
                // Category not found in cache
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }

            // Get the specific item from the menu
            IShopItem shopItem = menu.getParent().getItem(itemKey);
            if (shopItem == null) {
                // Item not found in category
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }

            // Clone the base item and apply language-specific data
            ItemStack itemToAdd = shopItem.getBaseItem().clone();

            // Apply language-specific name and lore
            ItemMeta meta = itemToAdd.getItemMeta();
            String namePath = "shop-items-messages." + shopItem.getCategoryKey() + ".content-item-" + shopItem.getItemKey() + "-name";

            meta.setDisplayName(cleanTierPlaceholders(
                    Utility.getMsg(player, namePath)
                            .replace("%bw_color%", Utility.getMsg(player, INVENTORY_COLOR_CODE))
                            .replace("{color}", Utility.getMsg(player, INVENTORY_COLOR_CODE))
                    )
            );
            meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE)
                    .stream().map(s -> s
                            .replace("{category}", cleanPlaceholders(Utility.getMsg(player, namePath)))
                    ).collect(Collectors.toList())
            );
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
            itemToAdd.setItemMeta(meta);
            // Add to player inventory at the slot
            inventory.setItem(slot, itemToAdd);
        }
    }

    private String cleanTierPlaceholders(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)\\s*(%bw_tier%|\\{tier\\})\\s*", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String cleanPlaceholders(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)\\s*(%bw_tier%|\\{tier\\}|%bw_color%|\\{color\\})\\s*", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
