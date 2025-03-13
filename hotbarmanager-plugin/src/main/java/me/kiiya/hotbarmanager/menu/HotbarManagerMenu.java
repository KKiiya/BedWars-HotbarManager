package me.kiiya.hotbarmanager.menu;

import com.andrei1058.bedwars.shop.ShopManager;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.support.VersionSupport;
import me.kiiya.hotbarmanager.config.MainConfig;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;
import static me.kiiya.hotbarmanager.utils.Utility.debug;

public class HotbarManagerMenu implements GUIHolder {

    private final VersionSupport vs;
    private final HotbarUtils hu;
    private final MainConfig mc;
    private final Player player;
    private Inventory inventory;

    public HotbarManagerMenu(Player player) {
        debug("Opening HotbarManager menu for " + player.getName());
        this.player = player;
        this.vs = HotbarManager.getVersionSupport();
        this.hu = HotbarUtils.getInstance();
        this.mc = HotbarManager.getMainConfig();
        try {
            createInventory();
            addContents();
            player.openInventory(getInventory());
        } catch (Exception e) {
            throw new RuntimeException("Error while opening the hotbar manager menu", e);
        }
        debug("HotbarManager menu opened for " + player.getName());
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
        PlayerInventory playerInventory = player.getInventory();
        ItemStack item = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        String tag = item != null  && item.getType() != Material.AIR ? vs.getItemTag(item, "hbm") : null;
        String cursorTag = cursor != null && cursor.getType() != Material.AIR ? vs.getItemTag(cursor, "hbm") : null;

        for (ItemStack pItem : playerInventory.getContents()) {
            if (pItem == null || pItem.getType() == Material.AIR) continue;
            String pTag = vs.getItemTag(pItem, "hbm");
            if (pTag == null) continue;
            pItem.setType(Material.AIR);
        }

        if (e.getAction() != InventoryAction.PICKUP_ALL
            && e.getAction() != InventoryAction.PICKUP_ONE
            && e.getAction() != InventoryAction.PLACE_ALL
            && e.getAction() != InventoryAction.PLACE_ONE
            && e.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
            e.setCancelled(true);
            e.setCursor(new ItemStack(Material.AIR));
            return;
        }

        if (e.getClickedInventory().getHolder() != this) {
            e.setCancelled(true);
            e.setCursor(new ItemStack(Material.AIR));
            return;
        }

        if (tag == null) {
            if (cursorTag != null && hu.getPosForSlot(e.getSlot()) != -1) {
                p.setSlotCategory(hu.getPosForSlot(e.getSlot()), Category.valueOf(cursorTag.toUpperCase()), true);
            } else e.setCancelled(true);
            e.setCursor(new ItemStack(Material.AIR));
            new HotbarManagerMenu(player);
            return;
        }

        switch (tag.toLowerCase()) {
            case "blocks":
            case "melee":
            case "tools":
            case "ranged":
            case "potions":
            case "utility":
            case "compass":
                ItemStack newItem = vs.setItemTag(e.getCurrentItem(), "hbm", tag);
                e.getInventory().setItem(e.getSlot(), newItem);
                e.setCursor(newItem);
                break;
            case "melee-slot":
            case "blocks-slot":
            case "tools-slot":
            case "ranged-slot":
            case "potions-slot":
            case "utility-slot":
            case "compass-slot":
            case "none-slot":
                String category = cursorTag == null ? "NONE" : cursorTag.toUpperCase();
                if (hu.getPosForSlot(e.getSlot()) == -1) {
                    e.setCancelled(true);
                    e.setCursor(new ItemStack(Material.AIR));
                    new HotbarManagerMenu(player);
                    return;
                }
                p.setSlotCategory(hu.getPosForSlot(e.getSlot()), Category.valueOf(category), true);
                e.setCursor(new ItemStack(Material.AIR));
                new HotbarManagerMenu(player);
                break;
            case "back":
                boolean isProxy = HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023;
                if (isProxy) p.getPlayer().closeInventory();
                else {
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
                        if (HotbarManager.getMainConfig().getString(BACK_COMMAND).equalsIgnoreCase("close")) p.getPlayer().closeInventory();
                        else p.getPlayer().performCommand(HotbarManager.getMainConfig().getString(BACK_COMMAND));
                    }
                }
                break;
            case "reset":
                if (cursorTag == null) {
                    p.resetHotbar();
                    e.setCancelled(true);
                    new HotbarManagerMenu(player);
                } else e.setCancelled(true);
                break;
            default:
                e.setCancelled(true);
                e.setCursor(new ItemStack(Material.AIR));
                new HotbarManagerMenu(player);
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
                isPlaying = HotbarManager.getBW2023Api().getArenaUtil().isPlaying(p.getPlayer());
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
        ItemStack reset = new ItemStack(Material.valueOf(mc.getString(HOTBAR_RESET_MATERIAL)), 1, (byte) mc.getInt(HOTBAR_RESET_DATA));
        ItemMeta reset_meta = reset.getItemMeta();
        reset_meta.setDisplayName((Utility.getMsg(player, INVENTORY_ITEMS_RESET_NAME)));
        reset_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_RESET_LORE));
        reset.setItemMeta(reset_meta);

        // SEPARATOR ITEM
        ItemStack separator = new ItemStack(Material.valueOf(mc.getString(SEPARATOR_MATERIAL)), 1, (byte) mc.getInt(SEPARATOR_DATA));
        ItemMeta separator_meta = separator.getItemMeta();
        separator_meta.setDisplayName((Utility.getMsg(player, SEPARATOR_NAME)));
        separator_meta.setLore(Utility.getListMsg(player, SEPARATOR_LORE));
        separator.setItemMeta(separator_meta);
        int[] positions = Arrays.stream(mc.getString("separator.positions").split(",")).mapToInt(Integer::parseInt).toArray();
        for (int i : positions) {
            inventory.setItem(i, vs.setItemTag(separator, "hbm", "separator"));
        }

        inventory.setItem(mc.getInt(BACK_POSITION), vs.setItemTag(back, "hbm", "back"));
        inventory.setItem(mc.getInt(HOTBAR_RESET_POSITION), vs.setItemTag(reset, "hbm", "reset"));
        // -------------------------------------------------------------------------- //



        // ---------------------------- CATEGORY ITEMS ---------------------------- //
        // BLOCKS CATEGORY
        ItemStack blocks = new ItemStack(Material.valueOf(mc.getString(CATEGORY_BLOCKS_MATERIAL)));
        blocks.setDurability((short) mc.getInt(CATEGORY_BLOCKS_DATA));
        if (vs.isPlayerHead(blocks)) blocks = Utility.getSkull(mc.getString(CATEGORY_BLOCKS_SKULL_URL));
        ItemMeta blocks_meta = blocks.getItemMeta();
        blocks_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_BLOCKS_NAME));
        blocks_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        blocks_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        blocks.setItemMeta(blocks_meta);

        //  MELEE CATEGORY
        ItemStack melee = new ItemStack(Material.valueOf(mc.getString(CATEGORY_MELEE_MATERIAL)));
        melee.setDurability((short) mc.getInt(CATEGORY_MELEE_DATA));
        if (vs.isPlayerHead(melee)) melee = Utility.getSkull(mc.getString(CATEGORY_MELEE_SKULL_URL));
        ItemMeta melee_meta = melee.getItemMeta();
        melee_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_MELEE_NAME));
        melee_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        melee_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        melee.setItemMeta(melee_meta);

        // TOOLS CATEGORY
        ItemStack tools = new ItemStack(Material.valueOf(mc.getString(CATEGORY_TOOLS_MATERIAL)));
        tools.setDurability((short) mc.getInt(CATEGORY_TOOLS_DATA));
        if (vs.isPlayerHead(tools)) tools = Utility.getSkull(mc.getString(CATEGORY_TOOLS_SKULL_URL));
        ItemMeta tools_meta = tools.getItemMeta();
        tools_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_TOOLS_NAME));
        tools_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        tools_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        tools.setItemMeta(tools_meta);

        // RANGED CATEGORY
        ItemStack ranged = new ItemStack(Material.valueOf(mc.getString(CATEGORY_RANGED_MATERIAL)));
        ranged.setDurability((short) mc.getInt(CATEGORY_RANGED_DATA));
        if (vs.isPlayerHead(ranged)) ranged = Utility.getSkull(mc.getString(CATEGORY_RANGED_SKULL_URL));
        ItemMeta ranged_meta = ranged.getItemMeta();
        ranged_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_RANGED_NAME));
        ranged_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        ranged_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ranged.setItemMeta(ranged_meta);

        // POTIONS CATEGORY
        ItemStack potions = new ItemStack(Material.valueOf(mc.getString(CATEGORY_POTIONS_MATERIAL)));
        potions.setDurability((short) mc.getInt(CATEGORY_POTIONS_DATA));
        if (vs.isPlayerHead(potions)) potions = Utility.getSkull(mc.getString(CATEGORY_POTIONS_SKULL_URL));
        ItemMeta potions_meta = potions.getItemMeta();
        potions_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_POTIONS_NAME));
        potions_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        potions_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        potions.setItemMeta(potions_meta);

        // SPECIALS CATEGORY
        ItemStack utility = new ItemStack(Material.valueOf(mc.getString(CATEGORY_SPECIALS_MATERIAL)));
        utility.setDurability((short) mc.getInt(CATEGORY_SPECIALS_DATA));
        if (vs.isPlayerHead(utility)) utility = Utility.getSkull(mc.getString(CATEGORY_SPECIALS_SKULL_URL));
        ItemMeta utility_meta = utility.getItemMeta();
        utility_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_SPECIALS_NAME));
        utility_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_LORE));
        utility_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        utility.setItemMeta(utility_meta);

        // COMPASS CATEGORY
        ItemStack compass = new ItemStack(Material.valueOf(mc.getString(CATEGORY_COMPASS_MATERIAL)));
        compass.setDurability((short) mc.getInt(CATEGORY_COMPASS_DATA));
        if (vs.isPlayerHead(compass)) compass = Utility.getSkull(mc.getString(CATEGORY_COMPASS_SKULL_URL));
        ItemMeta compass_meta = compass.getItemMeta();
        compass_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
        compass_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_COMPASS_LORE));
        compass_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        compass.setItemMeta(compass_meta);

        if (mc.getBoolean(CATEGORY_BLOCKS_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_BLOCKS_POSITION), vs.setItemTag(blocks, "hbm", "blocks"));
        if (mc.getBoolean(CATEGORY_MELEE_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_MELEE_POSITION), vs.setItemTag(melee, "hbm", "melee"));
        if (mc.getBoolean(CATEGORY_TOOLS_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_TOOLS_POSITION), vs.setItemTag(tools, "hbm", "tools"));
        if (mc.getBoolean(CATEGORY_RANGED_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_RANGED_POSITION), vs.setItemTag(ranged, "hbm", "ranged"));
        if (mc.getBoolean(CATEGORY_POTIONS_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_POTIONS_POSITION), vs.setItemTag(potions, "hbm", "potions"));
        if (mc.getBoolean(CATEGORY_SPECIALS_ENABLED)) inventory.setItem(mc.getInt(CATEGORY_SPECIALS_POSITION), vs.setItemTag(utility, "hbm", "utility"));
        switch (HotbarManager.getSupport()) {
            case BEDWARS1058:
            case BEDWARS2023:
                if (HotbarManager.isCompassAddon() && mc.getBoolean("enable-compass-support"))
                    inventory.setItem(mc.getInt(CATEGORY_COMPASS_POSITION), vs.setItemTag(compass, "hbm", "compass"));
                break;
            case BEDWARSPROXY:
            case BEDWARSPROXY2023:
                if (mc.getBoolean("enable-compass-support"))
                    inventory.setItem(mc.getInt(CATEGORY_COMPASS_POSITION), vs.setItemTag(compass, "hbm", "compass"));
                break;
        }
        // -------------------------------------------------------------------------- //

        loadInventoryHotbar(p);
    }

    private void loadInventoryHotbar(IHotbarPlayer p) {
        ItemStack category;
        ItemMeta category_meta;

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) slots.add(i);

        for (Integer slot : slots) {
            switch (p.getSlotCategory(slot)) {
                case NONE:
                    if (!mc.getBoolean(PLACEHOLDER_NONE_ENABLED)) {
                        category = new ItemStack(Material.AIR);
                        inventory.setItem(hu.getSlotForPos(slot), category);
                    }
                    else {
                        category = new ItemStack(Material.valueOf(mc.getString(PLACEHOLDER_NONE_MATERIAL)));
                        category.setDurability((short) mc.getInt(PLACEHOLDER_NONE_DATA));
                        if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(PLACEHOLDER_NONE_SKULL_URL));
                        category_meta = category.getItemMeta();
                        category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_PLACEHOLDER_NAME).replace("{slot}", String.valueOf(slot + 1)));
                        category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_PLACEHOLDER_LORE));
                        category.setItemMeta(category_meta);
                        inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "none-slot"));
                    }
                    break;
                case MELEE:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_MELEE_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_MELEE_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_MELEE_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_MELEE_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_MELEE))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "melee-slot"));
                    break;
                case BLOCKS:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_BLOCKS_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_BLOCKS_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_BLOCKS_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_BLOCKS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_BLOCKS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "blocks-slot"));
                    break;
                case TOOLS:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_TOOLS_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_TOOLS_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_TOOLS_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_TOOLS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_TOOLS))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "tools-slot"));
                    break;
                case RANGED:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_RANGED_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_RANGED_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_RANGED_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_RANGED_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_RANGED))).collect(Collectors.toList()));
                    category_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "ranged-slot"));
                    break;
                case POTIONS:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_POTIONS_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_POTIONS_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_POTIONS_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_POTIONS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_POTIONS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "potions-slot"));
                    break;
                case UTILITY:
                    category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_SPECIALS_MATERIAL)));
                    category.setDurability((short) mc.getInt(CATEGORY_SPECIALS_DATA));
                    if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_SPECIALS_SKULL_URL));
                    category_meta = category.getItemMeta();
                    category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_SPECIALS_NAME));
                    category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_SPECIALS))).collect(Collectors.toList()));
                    category.setItemMeta(category_meta);
                    inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "utility-slot"));
                    break;
                case COMPASS:
                    if (HotbarManager.getSupport() == Support.BEDWARSPROXY || HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
                        if (HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                            category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_COMPASS_MATERIAL)));
                            category.setDurability((short) mc.getInt(CATEGORY_COMPASS_DATA));
                            if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_COMPASS_SKULL_URL));
                            category_meta = category.getItemMeta();
                            category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
                            category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_COMPASS))).collect(Collectors.toList()));
                            category.setItemMeta(category_meta);
                            inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "compass-slot"));
                        }
                    } else {
                        if (HotbarManager.isCompassAddon() && HotbarManager.getMainConfig().getBoolean("enable-compass-support")) {
                            category = new ItemStack(Material.valueOf(mc.getString(CATEGORY_COMPASS_MATERIAL)));
                            category.setDurability((short) mc.getInt(CATEGORY_COMPASS_DATA));
                            if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(CATEGORY_COMPASS_SKULL_URL));
                            category_meta = category.getItemMeta();
                            category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_COMPASS_NAME));
                            category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_USED_LORE).stream().map(s -> s.replace("{category}", Utility.getMsg(player, MEANING_COMPASS))).collect(Collectors.toList()));
                            category.setItemMeta(category_meta);
                            inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "compass-slot"));
                        } else {
                            if (!mc.getBoolean(PLACEHOLDER_NONE_ENABLED)) {
                                category = new ItemStack(Material.AIR);
                                inventory.setItem(hu.getSlotForPos(slot), category);
                            }
                            else {
                                category = new ItemStack(Material.valueOf(mc.getString(PLACEHOLDER_NONE_MATERIAL)));
                                category.setDurability((short) mc.getInt(PLACEHOLDER_NONE_DATA));
                                if (vs.isPlayerHead(category)) category = Utility.getSkull(mc.getString(PLACEHOLDER_NONE_SKULL_URL));
                                category_meta = category.getItemMeta();
                                category_meta.setDisplayName(Utility.getMsg(player, INVENTORY_ITEMS_PLACEHOLDER_NAME).replace("{slot}", String.valueOf(slot + 1)));
                                category_meta.setLore(Utility.getListMsg(player, INVENTORY_ITEMS_PLACEHOLDER_LORE));
                                category.setItemMeta(category_meta);
                                inventory.setItem(hu.getSlotForPos(slot), vs.setItemTag(category, "hbm", "none-slot"));
                            }
                        }
                    }
                    break;
            }
        }
    }
}
