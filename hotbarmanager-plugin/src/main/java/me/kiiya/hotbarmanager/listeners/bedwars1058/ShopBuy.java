package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.team.TeamEnchant;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.configuration.Sounds;
import com.andrei1058.bedwars.shop.main.CategoryContent;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.shop.main.ShopIndex;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import com.andrei1058.bedwars.shop.quickbuy.QuickBuyElement;
import com.andrei1058.bedwars.shop.ShopManager;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
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

public class ShopBuy implements Listener {

    private final Set<UUID> processing = Collections.synchronizedSet(new HashSet<>());

    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        // MAIN VARIABLES
        Player p = e.getBuyer();
        PlayerInventory inv = p.getInventory();
        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(p);
        Category cat = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());
        List<Category> hotbar = hp.getHotbarAsList();

        // CHECKS
        if (cat == null || cat == Category.NONE) return;

        // ITEM AND SHOP VARIABLES
        VersionSupport vs = HotbarManager.getBW1058Api().getVersionSupport();
        ITeam t = e.getArena().getTeam(p);
        CategoryContent cc = (CategoryContent) e.getCategoryContent();
        String identifier = cc.getIdentifier();
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

        if (!hotbar.contains(cat)) return;

        try {
            processing.add(p.getUniqueId());
            for (int i = 0; i < 9; i++) {
                Category currentCategory = hotbar.get(i);
                ItemStack itemSlot = inv.getItem(i);
                if (currentCategory != cat) continue;

                if (BedWars.nms.isTool(itemSlot) && itemSlot != null) {
                    if (Utility.getItemCategory(itemSlot) == cat && !vs.getShopUpgradeIdentifier(itemSlot).equalsIgnoreCase(identifier)) continue;
                }

                if (indexViewers.contains(p.getUniqueId()) && element != null) cache.upgradeCachedItem(cc, element.getSlot());
                else cache.upgradeCachedItem(cc, cc.getSlot());

                ShopCache.CachedItem cachedItem = cache.getCachedItem(cc);
                IContentTier upgradableContent = cc.getContentTiers().get(cachedItem.getTier()-1);

                Material currency = upgradableContent.getCurrency();
                int price = upgradableContent.getPrice();

                ItemStack item = Utility.formatItemStack(upgradableContent.getBuyItemsList().get(0).getItemStack(), t);

                if (BedWars.nms.isSword(item)) {
                    inv.remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
                }

                if (BedWars.nms.isTool(item) || item.getType() == Material.SHEARS) {
                    unbreakable(item);
                    vs.setShopUpgradeIdentifier(item, identifier);
                }

                if (itemSlot != null) {
                    if (BedWars.nms.isSword(item) && currentCategory == Category.MELEE) {
                        unbreakable(item);
                        for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                            item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                        }

                        CategoryContent.takeMoney(p, currency, price);
                        if (Utility.isItemHigherTier(item, itemSlot)) {
                            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.addItem(itemSlot), 1L);
                            ItemStack finalItem = item;
                            int finalI = i;
                            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.setItem(finalI, finalItem), 2L);
                        } else inv.addItem(item);
                    } else if (vs.getShopUpgradeIdentifier(item) != null && vs.getShopUpgradeIdentifier(item).equals(identifier)) {
                        CategoryContent.takeMoney(p, currency, price);
                        ItemStack finalItem4 = item;
                        int finalI3 = i;
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.setItem(finalI3, vs.setShopUpgradeIdentifier(finalItem4, identifier)), 1L);
                    } else if (item.getType() == itemSlot.getType() && item.getDurability() == itemSlot.getDurability()) {
                        if (itemSlot.getAmount() + item.getAmount() > itemSlot.getType().getMaxStackSize()) continue;
                        else {
                            CategoryContent.takeMoney(p, currency, price);
                            ItemStack finalItem2 = item;
                            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> itemSlot.setAmount(itemSlot.getAmount() + finalItem2.getAmount()), 1L);
                        }
                    } else {
                        if (Utility.getItemCategory(itemSlot) == cat && !vs.getShopUpgradeIdentifier(itemSlot).equalsIgnoreCase(identifier)) continue;

                        if (cc.isPermanent()) {
                            unbreakable(item);
                            item = vs.setShopUpgradeIdentifier(item, identifier);
                        }

                        if (BedWars.nms.isSword(item)) {
                            unbreakable(item);
                            for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                                item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                            }
                        }

                        CategoryContent.takeMoney(p, currency, price);
                        ItemStack itemToAdd = inv.getItem(i);
                        if (itemToAdd != null) Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.addItem(itemToAdd), 1L);
                        int finalI2 = i;
                        ItemStack finalItem3 = item;
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.setItem(finalI2, finalItem3), 2L);
                    }
                } else {
                    if (cc.isPermanent()) {
                        unbreakable(item);
                        item = vs.setShopUpgradeIdentifier(item, identifier);
                    }

                    if (BedWars.nms.isSword(item)) {
                        unbreakable(item);
                        for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                            item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                        }
                    }

                    CategoryContent.takeMoney(p, currency, price);
                    int finalI1 = i;
                    ItemStack finalItem1 = item;
                    Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.setItem(finalI1, finalItem1), 1L);
                }

                e.setCancelled(true);
                Sounds.playSound(buySound, p);
                p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                        .replace("{prefix}", Utility.getMsg(p, "prefix"))
                        .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                        .replace("{color}", "")
                        .replace("{tier}", !BedWars.nms.isTool(item) ? "" : CategoryContent.getRomanNumber(cachedItem.getTier())));
                ShopManager.shop.open(p, quickBuyCache, false);
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            processing.remove(p.getUniqueId());
        }
    }

    public void unbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        BedWars.nms.setUnbreakable(meta);
        itemStack.setItemMeta(meta);
    }
}