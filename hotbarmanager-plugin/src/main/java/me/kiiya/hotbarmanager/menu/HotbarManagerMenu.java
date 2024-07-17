package me.kiiya.hotbarmanager.menu;

import com.andrei1058.bedwars.shop.ShopManager;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class HotbarManagerMenu implements GUIHolder {
    private final Player player;
    private Inventory inventory;

    public HotbarManagerMenu(Player player) {
        this.player = player;
        try {
            createInventory();
            addContents();
            player.openInventory(getInventory());
        } catch (Exception e) {
            throw new RuntimeException("Error while opening the hotbar manager menu", e);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void createInventory() {
        inventory = Bukkit.createInventory(this, 54, Utility.getMsg(player, INVENTORY_NAME));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(player);

        if (e.getInventory() != e.getClickedInventory() || e.getClickedInventory() == player.getInventory()){
            e.setCursor(new ItemStack(Material.AIR));
            e.getCursor().setType(Material.AIR);
            e.setCancelled(true);
            return;
        }

        if (e.getAction() != InventoryAction.PICKUP_ALL
            && e.getAction() != InventoryAction.PICKUP_ONE
            && e.getAction() != InventoryAction.PLACE_ALL
            && e.getAction() != InventoryAction.PLACE_ONE) {
            e.setCursor(new ItemStack(Material.AIR));
            e.setCancelled(true);
            return;
        }

        switch (e.getSlot()) {
            case 48:
                if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
                    p.getPlayer().closeInventory();
                } else {
                    boolean isPlaying = false;
                    if (HotbarManager.getSupport() == Support.BEDWARS1058) {
                        isPlaying = HotbarManager.getBW1058Api().getArenaUtil().isPlaying(p.getPlayer());
                    } else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
                        isPlaying = HotbarManager.bw2023Api.getArenaUtil().isPlaying(p.getPlayer());
                    }

                    if (isPlaying) {
                        if (HotbarManager.getSupport() == Support.BEDWARS1058) {
                            ShopManager.shop.open(p.getPlayer(), PlayerQuickBuyCache.getQuickBuyCache(p.getPlayer().getUniqueId()), false);
                        } else {
                            com.tomkeuper.bedwars.shop.ShopManager.shop.open(p.getPlayer(), com.tomkeuper.bedwars.shop.quickbuy.PlayerQuickBuyCache.getInstance().getQuickBuyCache(p.getPlayer().getUniqueId()), false);
                        }
                    } else {
                        if (HotbarManager.getMainConfig().getString(BACK_COMMAND).equalsIgnoreCase("close")) {
                            p.getPlayer().closeInventory();
                        } else {
                            p.getPlayer().performCommand(HotbarManager.getMainConfig().getString(BACK_COMMAND));
                        }
                    }
                }
                break;
            case 50:
                p.resetHotbar();
                e.setCancelled(true);
                new HotbarManagerMenu(player);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    e.getInventory().setItem(e.getSlot(), e.getCurrentItem());
                    e.setCursor(e.getCurrentItem());
                }
                break;
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
                int slot = HotbarUtils.getPosForSlot(e.getSlot());
                if (e.getCursor().getType() == Material.AIR || e.getCursor().getType() == null) {
                    p.setSlotCategory(slot, Category.NONE);
                }
                if (e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE || e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                    if (e.getCursor().getType() == Material.HARD_CLAY) {
                        p.setSlotCategory(slot, Category.BLOCKS);
                    } else if (e.getCursor().getType() == Material.GOLD_SWORD) {
                        p.setSlotCategory(slot, Category.MELEE);
                    } else if (e.getCursor().getType() == Material.IRON_PICKAXE) {
                        p.setSlotCategory(slot, Category.TOOLS);
                    } else if (e.getCursor().getType() == Material.BOW) {
                        p.setSlotCategory(slot, Category.RANGED);
                    } else if (e.getCursor().getType() == Material.BREWING_STAND_ITEM) {
                        p.setSlotCategory(slot, Category.POTIONS);
                    } else if (e.getCursor().getType() == Material.TNT) {
                        p.setSlotCategory(slot, Category.UTILITY);
                    } else if (e.getCursor().getType() == Material.COMPASS) {
                        p.setSlotCategory(slot, Category.COMPASS);
                    }
                }
                e.setCursor(new ItemStack(Material.AIR));
                new HotbarManagerMenu(player);
                break;
            default:
                e.setCancelled(true);
                break;
        }
    }

    @Override
    public void onInventoryDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    private void addContents() {
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(player);


        // ---------------------------- UTILS ITEMS ---------------------------- //

        // BACK ITEM
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta back_meta = back.getItemMeta();
        if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_LOBBY_NAME)));
            back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_LOBBY_LORE));
        } else {
            boolean isPlaying = false;
            if (HotbarManager.getSupport() == Support.BEDWARS1058) {
                isPlaying = HotbarManager.getBW1058Api().getArenaUtil().isPlaying(p.getPlayer());
            } else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
                isPlaying = HotbarManager.bw2023Api.getArenaUtil().isPlaying(p.getPlayer());
            }

            if (isPlaying) {
                back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_QUICK_BUY_NAME)));
                back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_QUICK_BUY_LORE));
            } else {
                back_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_BACK_LOBBY_NAME)));
                back_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_BACK_LOBBY_LORE));
            }
        }
        back.setItemMeta(back_meta);

        // RESET ITEM
        ItemStack reset = new ItemStack(Material.BARRIER);
        ItemMeta reset_meta = reset.getItemMeta();
        reset_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_RESET_NAME)));
        reset_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_RESET_LORE));
        reset.setItemMeta(reset_meta);

        // SEPARATOR ITEM
        ItemStack separator = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
        ItemMeta separator_meta = separator.getItemMeta();
        separator_meta.setDisplayName((Utility.getMsg(player, SEPARATOR_NAME)));
        separator_meta.setLore(Utility.getListMsg(player, SEPARATOR_LORE));
        separator.setItemMeta(separator_meta);

        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, separator);
        }

        inventory.setItem(48, back);
        inventory.setItem(50, reset);
        // -------------------------------------------------------------------------- //



        // ---------------------------- CATEGORY ITEMS ---------------------------- //
        // BLOCKS CATEGORY
        ItemStack blocks = new ItemStack(Material.HARD_CLAY);
        ItemMeta blocks_meta = blocks.getItemMeta();
        blocks_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_BLOCKS_NAME));
        blocks_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        blocks_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        blocks.setItemMeta(blocks_meta);

        //  MELEE CATEGORY
        ItemStack melee = new ItemStack(Material.GOLD_SWORD);
        ItemMeta melee_meta = melee.getItemMeta();
        melee_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_MELEE_NAME));
        melee_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        melee_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        melee.setItemMeta(melee_meta);

        // TOOLS CATEGORY
        ItemStack tools = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta tools_meta = tools.getItemMeta();
        tools_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_TOOLS_NAME));
        tools_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        tools_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        tools.setItemMeta(tools_meta);

        // RANGED CATEGORY
        ItemStack ranged = new ItemStack(Material.BOW);
        ItemMeta ranged_meta = ranged.getItemMeta();
        ranged_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_RANGED_NAME));
        ranged_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        ranged_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ranged.setItemMeta(ranged_meta);

        // POTIONS CATEGORY
        ItemStack potions = new ItemStack(Material.BREWING_STAND_ITEM);
        ItemMeta potions_meta = potions.getItemMeta();
        potions_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_POTIONS_NAME));
        potions_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        potions_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        potions.setItemMeta(potions_meta);

        // SPECIALS CATEGORY
        ItemStack utility = new ItemStack(Material.TNT);
        ItemMeta utility_meta = utility.getItemMeta();
        utility_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_SPECIALS_NAME));
        utility_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        utility_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        utility.setItemMeta(utility_meta);

        // COMPASS CATEGORY
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compass_meta = compass.getItemMeta();
        compass_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
        compass_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_COMPASS_LORE));
        compass_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        compass.setItemMeta(compass_meta);

        if (HotbarManager.getMainConfig().getBoolean(ENABLE_BLOCKS_CATEGORY)) inventory.setItem(10, Utility.setItemTag(blocks, "hbm", "blocks"));
        if (HotbarManager.getMainConfig().getBoolean(ENABLE_MELEE_CATEGORY)) inventory.setItem(11, Utility.setItemTag(melee, "hbm", "melee"));
        if (HotbarManager.getMainConfig().getBoolean(ENABLE_TOOLS_CATEGORY)) inventory.setItem(12, Utility.setItemTag(tools, "hbm", "tools"));
        if (HotbarManager.getMainConfig().getBoolean(ENABLE_RANGED_CATEGORY)) inventory.setItem(13, Utility.setItemTag(ranged, "hbm", "ranged"));
        if (HotbarManager.getMainConfig().getBoolean(ENABLE_POTIONS_CATEGORY)) inventory.setItem(14, Utility.setItemTag(potions, "hbm", "potions"));
        if (HotbarManager.getMainConfig().getBoolean(ENABLE_SPECIALS_CATEGORY))inventory.setItem(15, Utility.setItemTag(utility, "hbm", "utility"));
        switch (HotbarManager.getSupport()) {
            case BEDWARS1058:
            case BEDWARS2023:
                if (HotbarManager.isCompassAddon() && HotbarManager.mainConfig.getBoolean("enable-compass-support"))
                    inventory.setItem(16, Utility.setItemTag(compass, "hbm", "compass"));
                break;
            case BEDWARSPROXY:
            case BEDWARSPROXY2023:
                if (HotbarManager.mainConfig.getBoolean("enable-compass-support"))
                    inventory.setItem(16, Utility.setItemTag(compass, "hbm", "compass"));
                break;
        }
        // -------------------------------------------------------------------------- //

        loadInventoryHotbar(p);
    }

    private void loadInventoryHotbar(IHotbarPlayer p) {
        ItemStack category;
        ItemMeta category_meta;

        List<Integer> slots = new ArrayList<>();
        slots.add(0);
        slots.add(1);
        slots.add(2);
        slots.add(3);
        slots.add(4);
        slots.add(5);
        slots.add(6);
        slots.add(7);
        slots.add(8);

        for (Integer slot : slots) {
            switch (p.getSlotCategory(slot)) {
                case NONE:
                    category = new ItemStack(Material.AIR);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case MELEE:
                    category = new ItemStack(Material.GOLD_SWORD);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_MELEE_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_MELEE))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case BLOCKS:
                    category = new ItemStack(Material.HARD_CLAY);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_BLOCKS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_BLOCKS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case TOOLS:
                    category = new ItemStack(Material.IRON_PICKAXE);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_TOOLS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_TOOLS))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case RANGED:
                    category = new ItemStack(Material.BOW);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_RANGED_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_RANGED))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case POTIONS:
                    category = new ItemStack(Material.BREWING_STAND_ITEM);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_POTIONS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_POTIONS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case UTILITY:
                    category = new ItemStack(Material.TNT);
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_SPECIALS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_SPECIALS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                    break;
                case COMPASS:
                    if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
                        if (HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                            category = new ItemStack(Material.COMPASS);
                            category_meta = category.getItemMeta();
                            category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
                            category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_COMPASS))).collect(Collectors.toList()));
                            category.setItemMeta(category_meta);
                            inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                        }
                    } else {
                        if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                            category = new ItemStack(Material.COMPASS);
                            category_meta = category.getItemMeta();
                            category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
                            category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_COMPASS))).collect(Collectors.toList()));
                            category.setItemMeta(category_meta);
                            inventory.setItem(HotbarUtils.getSlotForPos(slot), category);
                        }
                    }
                    break;
            }
        }
    }
}
