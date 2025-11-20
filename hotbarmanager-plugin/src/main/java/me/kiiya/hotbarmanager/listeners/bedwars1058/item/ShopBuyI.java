package me.kiiya.hotbarmanager.listeners.bedwars1058.item;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamEnchant;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.configuration.Sounds;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.shop.ShopManager;
import com.andrei1058.bedwars.shop.main.CategoryContent;
import com.andrei1058.bedwars.shop.main.ShopCategory;
import com.andrei1058.bedwars.shop.main.ShopIndex;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import com.andrei1058.bedwars.shop.quickbuy.QuickBuyElement;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.events.HotbarItemSetEvent;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.kiiya.hotbarmanager.utils.Utility.debug;

public class ShopBuyI implements Listener {

    private final Set<UUID> processing = Collections.synchronizedSet(new HashSet<>());

    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        VersionSupport vs = HotbarManager.getBW1058Api().getVersionSupport();

        // MAIN VARIABLES
        Player p = e.getBuyer();
        PlayerInventory inv = p.getInventory();
        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(p);
        String identifier = e.getCategoryContent().getIdentifier();
        List<String> hotbar = hp.getHotgarAsStringList();

        // CHECKS
        if (identifier == null || identifier.isEmpty()) return;

        // ITEM AND SHOP VARIABLES
        ITeam t = e.getArena().getTeam(p);
        CategoryContent cc = (CategoryContent) e.getCategoryContent();
        PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(p.getUniqueId());
        if (quickBuyCache == null) quickBuyCache = new PlayerQuickBuyCache(p);
        QuickBuyElement element = quickBuyCache.getElements().stream()
                .filter(el -> el.getCategoryContent().getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
        List<UUID> indexViewers = ShopIndex.getIndexViewers();
        ShopCache cache = ShopCache.getShopCache(p.getUniqueId());

        // SOUNDS
        String buySound = "shop-bought";

        if (!hotbar.contains(identifier)) return;
        if (processing.contains(p.getUniqueId())) return;

        try {
            processing.add(p.getUniqueId());
            for (int i = 0; i < 9; i++) {
                debug("Checking slot " + i);
                String currentPath = hotbar.get(i);
                ItemStack itemSlot = inv.getItem(i);
                if (!currentPath.equals(identifier)) continue;

                if ((BedWars.nms.isTool(itemSlot) || itemSlot.getType() == Material.SHEARS) && itemSlot != null) {
                    debug("Item is upgradable");
                    if (!vs.getShopUpgradeIdentifier(itemSlot).equalsIgnoreCase(identifier)) {
                        debug("Item is the same category but doesn't have the same identifier");
                        continue;
                    }
                }

                HotbarItemSetEvent event = new HotbarItemSetEvent(p, identifier, i);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    debug("Event was cancelled for slot " + i);
                    return;
                }

                if (indexViewers.contains(p.getUniqueId()) && element != null) cache.upgradeCachedItem(cc, element.getSlot());
                else cache.upgradeCachedItem(cc, cc.getSlot());

                ShopCache.CachedItem cachedItem = cache.getCachedItem(cc);
                IContentTier upgradableContent = cc.getContentTiers().get(cachedItem.getTier()-1);

                Material currency = upgradableContent.getCurrency();
                int totalPlayerMoney = CategoryContent.calculateMoney(p, currency);
                int price = upgradableContent.getPrice();

                ItemStack item = Utility.formatItemStack(upgradableContent.getBuyItemsList().get(0).getItemStack(), t);

                if (BedWars.nms.isSword(item)) {
                    inv.remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
                }

                if (BedWars.nms.isTool(item) || item.getType() == Material.SHEARS || cc.isPermanent()) {
                    unbreakable(item);
                    vs.setShopUpgradeIdentifier(item, identifier);
                }

                if (itemSlot != null && itemSlot.getType() != Material.AIR) {
                    if (BedWars.nms.isSword(item)) {
                        debug("Item is a sword and the category is MELEE");
                        unbreakable(item);
                        for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                            item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                        }

                        if (BedWars.nms.isSword(itemSlot) && Utility.isItemHigherTier(item, itemSlot)) {
                            debug("Item is a sword and the item in the slot is lower tier, replacing it");
                            ItemStack itemToAdd = inv.getItem(i);
                            if (itemToAdd != null) itemToAdd = itemToAdd.clone();

                            inv.setItem(i, item);
                            p.updateInventory();

                            if (itemToAdd != null) {
                                inv.addItem(itemToAdd);
                                p.updateInventory();
                            }
                        } else {
                            debug("Item is a sword and the item in the slot is higher tier, adding it to the inventory");
                            inv.addItem(item);
                            p.updateInventory();
                        }
                    } else if (vs.getShopUpgradeIdentifier(itemSlot) != null && vs.getShopUpgradeIdentifier(itemSlot).equals(identifier)) {
                        debug("Item has the same identifier, replacing...");
                        inv.setItem(i, vs.setShopUpgradeIdentifier(item, identifier));
                        p.updateInventory();
                    } else if (item.getType() == itemSlot.getType() && item.getDurability() == itemSlot.getDurability()) {
                        if (itemSlot.getAmount() + item.getAmount() > itemSlot.getType().getMaxStackSize()) {
                            debug("Item is the same type and durability, but the amount is higher than the max stack size");
                            continue;
                        } else {
                            debug("Item is the same type and durability, adding the amount");
                            itemSlot.setAmount(itemSlot.getAmount() + item.getAmount());
                            p.updateInventory();
                        }
                    } else {
                        if ((vs.getShopUpgradeIdentifier(itemSlot) == null || !vs.getShopUpgradeIdentifier(itemSlot).equalsIgnoreCase(identifier))) continue;
                        debug("Item is same category but different identifier or no identifier");

                        if (cc.isPermanent()) {
                            debug("Item is permanent");
                            unbreakable(item);
                            item = vs.setShopUpgradeIdentifier(item, identifier);
                        }

                        if (BedWars.nms.isSword(item)) {
                            debug("Item is a sword");
                            unbreakable(item);
                            for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                                item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                            }
                        }

                        ItemStack itemToAdd = inv.getItem(i);
                        if (itemToAdd != null) itemToAdd = itemToAdd.clone();

                        inv.setItem(i, item);
                        p.updateInventory();

                        if (itemToAdd != null) {
                            debug("Adding replaced item to inventory");
                            inv.addItem(itemToAdd);
                            p.updateInventory();
                        }
                    }
                } else {
                    debug("No item was found in slot " + i);
                    if (cc.isPermanent()) {
                        debug("Item is permanent");
                        unbreakable(item);
                        item = vs.setShopUpgradeIdentifier(item, identifier);
                    }

                    if (BedWars.nms.isSword(item)) {
                        debug("Item is a sword");
                        unbreakable(item);
                        for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                            item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                        }
                    }

                    debug("Setting item in slot " + i);
                    inv.setItem(i, item);
                    p.updateInventory();
                }

                e.setCancelled(true);

                debug("Removing money from player with currency " + currency + " and price " + price);
                CategoryContent.takeMoney(p, currency, price);
                Sounds.playSound(buySound, p);
                p.updateInventory();

                int finalPlayerMoney = CategoryContent.calculateMoney(p, currency);
                int expectedPlayerMoney = totalPlayerMoney - price;
                debug("Starter money: " + totalPlayerMoney + ", expected money: " + expectedPlayerMoney + ", final money: " + finalPlayerMoney);

                if (expectedPlayerMoney > finalPlayerMoney) {
                    debug("Final money (" + finalPlayerMoney + ") is lower than expected money (" + expectedPlayerMoney + "), adding...");
                    ItemStack addMissingMoney = new ItemStack(currency, expectedPlayerMoney - finalPlayerMoney);
                    inv.addItem(addMissingMoney);
                } else if (finalPlayerMoney > expectedPlayerMoney) {
                    debug("Final money (" + finalPlayerMoney + ") is higher than expected money (" + expectedPlayerMoney + "), removing...");
                    ItemStack removeExtraMoney = new ItemStack(currency, finalPlayerMoney - expectedPlayerMoney);
                    inv.removeItem(removeExtraMoney);
                }
                debug("Money took successfully");

                p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                        .replace("{prefix}", Utility.getMsg(p, "prefix"))
                        .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                        .replace("{color}", "")
                        .replace("{tier}", !BedWars.nms.isTool(item) ? "" : CategoryContent.getRomanNumber(cachedItem.getTier())));
                if (indexViewers.contains(p.getUniqueId())) ShopManager.shop.open(p, quickBuyCache, false);
                else {
                    for (ShopCategory sc : ShopManager.shop.getCategoryList()) {
                        String ccId = cc.getIdentifier().split("\\.")[0];
                        String scId =  sc.getCategoryContentList().get(0).getIdentifier().split("\\.")[0];
                        if (ccId.equals(scId)) {
                            sc.open(p, ShopManager.shop, cache);
                            break;
                        }
                    }
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            processing.remove(p.getUniqueId());
        }
    }

    private int getPrice(CategoryContent cc, int tier) {
        return cc.getContentTiers().get(tier-1).getPrice();
    }

    private int getPrice(IContentTier contentTier) {
        return contentTier.getPrice();
    }

    private void unbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        BedWars.nms.setUnbreakable(meta);
        itemStack.setItemMeta(meta);
    }
}