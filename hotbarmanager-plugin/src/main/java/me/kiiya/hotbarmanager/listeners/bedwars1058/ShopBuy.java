package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.configuration.Sounds;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.shop.main.CategoryContent;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShopBuy implements Listener {
    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        // MAIN VARIABLES
        Player p = e.getBuyer();
        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(e.getBuyer());
        Category cat = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());
        List<Category> hotbar = hp.getHotbarAsList();

        // CHECKS
        if (cat == null || cat == Category.NONE) return;

        // ITEM BOUGHT VARIABLES
        ITeam t = e.getArena().getTeam(p);
        ICategoryContent cc = e.getCategoryContent();
        String identifier = cc.getIdentifier();
        IContentTier content = cc.getContentTiers().get(0);
        ShopCache cache = ShopCache.getShopCache(p.getUniqueId());
        AtomicReference<ShopCache.CachedItem> cachedItem = new AtomicReference<>(cache.getCachedItem(identifier));
        IContentTier tier = cachedItem.get() == null ? cc.getContentTiers().get(0) : e.getCategoryContent().getContentTiers().get(cachedItem.get().getTier()-1);
        Material currency = tier.getCurrency();
        int price = content.getPrice();
        ItemStack item = Utility.formatItemStack(content.getBuyItemsList().get(0).getItemStack(), t);
        item = BedWars.nms.addCustomData(item, "");
        if (BedWars.nms.isSword(item)) {
            p.getInventory().remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
        }
        if (BedWars.nms.isTool(item)) {
            ItemMeta meta = item.getItemMeta();
            BedWars.nms.setUnbreakable(meta);
            item.setItemMeta(meta);
        }

        // SOUNDS
        String buySound = "shop-bought";


        if (!hotbar.contains(cat)) return;

        if (p.getInventory().firstEmpty() == -1) {
            e.setCancelled(true);
            return;
        }

        try {
            for (int i = 0; i < 9; i++) {
                if (hotbar.get(i) == cat) {
                    if (p.getInventory().getItem(i) != null) {
                        if (HotbarManager.getBW1058Api().getVersionSupport().getShopUpgradeIdentifier(item) != null && HotbarManager.getBW1058Api().getVersionSupport().getShopUpgradeIdentifier(item).equals(identifier)) {
                            cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                            cachedItem.set(cache.getCachedItem(identifier));
                            item = cc.getContentTiers().get(cachedItem.get().getTier()-1).getBuyItemsList().get(0).getItemStack();
                            p.getInventory().setItem(i-1, HotbarManager.getBW1058Api().getVersionSupport().setShopUpgradeIdentifier(item, identifier));
                        } else {
                            if (item.getType() == p.getInventory().getItem(i).getType() && item.getDurability() == p.getInventory().getItem(i).getDurability()) {
                                if (p.getInventory().getItem(i).getAmount() >= p.getInventory().getItem(i).getType().getMaxStackSize())
                                    continue;
                                else {
                                    if (p.getInventory().getItem(i).getAmount() + item.getAmount() > p.getInventory().getItem(i).getType().getMaxStackSize()) {
                                        int finalI = i;
                                        int restAmount = p.getInventory().getItem(i).getAmount() - p.getInventory().getItem(i).getType().getMaxStackSize() + item.getAmount();
                                        ItemStack finalItem = item;
                                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                                            p.getInventory().getItem(finalI).setAmount(p.getInventory().getItem(finalI).getType().getMaxStackSize());
                                            p.getInventory().addItem(BedWars.nms.addCustomData(Utility.formatItemStack(new ItemStack(finalItem.getType(), restAmount), t), ""));
                                        }, 1L);
                                    } else {
                                        p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() + item.getAmount());
                                    }
                                }
                            } else {
                                if (Utility.getItemCategory(p.getInventory().getItem(i)) == cat) continue;
                                ItemStack addedItem = p.getInventory().getItem(i);
                                int finalI = i;
                                AtomicReference<ItemStack> finalItem1 = new AtomicReference<>(item);
                                Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                                    if (cc.isPermanent()) {
                                        cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                                        cachedItem.set(cache.getCachedItem(identifier));
                                        finalItem1.set(cc.getContentTiers().get(cachedItem.get().getTier() - 1).getBuyItemsList().get(0).getItemStack());
                                        finalItem1.set(Utility.formatItemStack(finalItem1.get(), t));
                                        p.getInventory().setItem(finalI, HotbarManager.getBW1058Api().getVersionSupport().setShopUpgradeIdentifier(finalItem1.get(), identifier));
                                    } else {
                                        p.getInventory().setItem(finalI, finalItem1.get());
                                    }
                                }, 1L);
                                Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().addItem(addedItem), 2L);
                            }
                        }
                    } else {
                        if (cc.isPermanent()) {
                            cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                            cachedItem.set(cache.getCachedItem(identifier));
                            item = cc.getContentTiers().get(cachedItem.get().getTier()-1).getBuyItemsList().get(0).getItemStack();
                            Utility.formatItemStack(item, t);
                            ItemMeta meta = item.getItemMeta();
                            BedWars.nms.setUnbreakable(meta);
                            item.setItemMeta(meta);
                            p.getInventory().setItem(i, HotbarManager.getBW1058Api().getVersionSupport().setShopUpgradeIdentifier(item, identifier));
                        } else {
                            p.getInventory().setItem(i, item);
                        }
                    }

                    Sounds.playSound(buySound, p);
                    p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                            .replace("{prefix}", Utility.getMsg(p, "prefix"))
                            .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                            .replace("{color}", "")
                            .replace("{tier}", cachedItem.get() == null ? "" : CategoryContent.getRomanNumber(cachedItem.get().getTier())));
                    CategoryContent.takeMoney(p, currency, price);
                    e.setCancelled(true);
                    break;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
